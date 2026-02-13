package com.concept.service;

import com.concept.db.RequestDatabaseService;
import com.concept.io.input.FileHandler;
import com.concept.io.input.ValidationException;
import com.concept.ip.validation.IpAddressValidation;
import com.concept.ip.validation.ValidationResult;
import com.concept.utils.NanoTimeSupplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class EntryFileService {

    private final Supplier<String> requestIdSupplier;
    private final IpAddressValidation ipAddressValidation;
    private final FileHandler fileHandler;
    private final RequestDatabaseService requestDatabaseService;
    private final Clock clock;
    private final NanoTimeSupplier nanoTimeSupplier;

    public ServiceResponse handle(String ipAddress, String requestUrl, MultipartFile file) {
        final ServiceResponse response;
        final long startNs = nanoTimeSupplier.get();
        final LocalDateTime requestTime = LocalDateTime.now(clock);
        final String requestId = requestIdSupplier.get();

        final ValidationResult validationResult = ipAddressValidation.validate(ipAddress);

        if(validationResult.succeeded()) {
            response = processFile(file, requestId);
        } else if(validationResult.blocked()) {
            response = new ServiceResponse(HttpStatus.FORBIDDEN, validationResult.message());
        } else {
            response = new ServiceResponse(HttpStatus.SERVICE_UNAVAILABLE, validationResult.message());
        }

        final long elapsedTimeMs = (long)((nanoTimeSupplier.get()- startNs) / 1_000_000.0);
        saveRequest(response.status(), requestUrl, requestId, ipAddress, requestTime, validationResult, elapsedTimeMs);

        return response;
    }

    private ServiceResponse processFile(MultipartFile file, String requestId) {
        try {
            final Path outcomePath = fileHandler.handle(file, requestId);
            return new ServiceResponse(HttpStatus.OK, outcomePath);
        } catch(ValidationException e) {
            log.error("Validation error processing file={} for requestId={}", file.getOriginalFilename(), requestId, e);
            return new ServiceResponse(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch(IOException e) {
            log.error("Error processing file={} for requestId={}", file.getOriginalFilename(), requestId, e);
            return new ServiceResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void saveRequest(HttpStatus httpStatus, String requestUrl, String requestId, String ipAddress,
                             LocalDateTime requestTime, ValidationResult validationResult, long elapsedTimeMs) {
        requestDatabaseService.saveRequest(requestId, requestUrl, requestTime, httpStatus.value(), ipAddress,
                validationResult.ipInfo().countryCode(), validationResult.ipInfo().isp(), elapsedTimeMs);
    }
}
