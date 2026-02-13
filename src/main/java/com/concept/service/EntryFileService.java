package com.concept.service;

import com.concept.db.RequestDatabaseService;
import com.concept.io.input.MultipartFileHandler;
import com.concept.ip.validation.IpAddressValidation;
import com.concept.ip.validation.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntryFileService {

    private final Supplier<String> requestIdSupplier;
    private final IpAddressValidation ipAddressValidation;
    private final MultipartFileHandler fileHandler;
    private final RequestDatabaseService requestDatabaseService;

    public ServiceResponse handle(String ipAddress, MultipartFile file, String requestUrl) {

        final long start = System.nanoTime();
        final LocalDateTime requestTime = LocalDateTime.now();
        final String requestId = requestIdSupplier.get();

        final ValidationResult validationResult = ipAddressValidation.validate(ipAddress);

        final ServiceResponse response = processFile(file, validationResult, requestId);

        requestDatabaseService.saveRequest(requestId, requestUrl, requestTime, response.status().value(),
                validationResult.ipInfo().countryCode(), validationResult.ipInfo().isp(),
                (long)((System.nanoTime() - start) / 1_000_000.0));

        return response;
    }

    private ServiceResponse processFile(MultipartFile file, ValidationResult validationResult, String requestId) {
        if(validationResult.succeeded()) {
            try {

                final Path outcomePath = fileHandler.handle(file, requestId);
                return new ServiceResponse(HttpStatus.OK, Optional.of(outcomePath));

            } catch(IOException e) {
                log.error("Error processing file={} for requestId={}", file.getOriginalFilename(), requestId, e);
                return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else if(validationResult.blocked()) {
            return new ServiceResponse(HttpStatus.FORBIDDEN, validationResult.message());
        } else {
            return new ServiceResponse(HttpStatus.SERVICE_UNAVAILABLE, validationResult.message());
        }
    }
}
