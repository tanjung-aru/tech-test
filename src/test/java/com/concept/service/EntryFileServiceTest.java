package com.concept.service;

import com.concept.db.RequestDatabaseService;
import com.concept.io.input.FileHandler;
import com.concept.io.input.ValidationException;
import com.concept.ip.rest.IpInfo;
import com.concept.ip.validation.IpAddressValidation;
import com.concept.ip.validation.ValidationResult;
import com.concept.utils.NanoTimeSupplier;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EntryFileServiceTest {

    private static final String IP_ADDRESS = "1.2.3.4";
    private static final String REQUEST_ID = "REQUEST-FOO123";
    private static final String REQUEST_URL = "/api/upload-entry-file";
    private static final IpInfo IP_INFO = new IpInfo("UK", "IBM");
    private static final Path OUTCOME_PATH = Paths.get("OutcomeFile.json");

    @Mock
    private Supplier<String> requestIdSupplier;

    @Mock
    private IpAddressValidation ipAddressValidation;

    @Mock
    private FileHandler fileHandler;

    @Mock
    private RequestDatabaseService requestDatabaseService;

    @Mock
    private MultipartFile multipartFile;

    @Spy
    private Clock clock = Clock.fixed(
            Instant.parse("2026-01-01T10:00:00Z"),
            ZoneId.of("UTC")
    );

    @Mock
    private NanoTimeSupplier nanoTimeSupplier;

    private EntryFileService entryFileService;

    @BeforeEach
    void setUp() {
        when(requestIdSupplier.get()).thenReturn(REQUEST_ID);
        when(nanoTimeSupplier.get()).thenReturn(0L).thenReturn(100_000_000L);
        entryFileService = new EntryFileService(requestIdSupplier, ipAddressValidation, fileHandler, requestDatabaseService, clock, nanoTimeSupplier);
    }

    @Test
    void assert_when_ip_not_blocked_and_file_is_processed_response_is_OK() throws IOException {
        when(ipAddressValidation.validate(any())).thenReturn(ValidationResult.success(IP_INFO));
        when(fileHandler.handle(any(), anyString())).thenReturn(OUTCOME_PATH);

        ServiceResponse serviceResponse = entryFileService.handle(IP_ADDRESS, REQUEST_URL, multipartFile);

        Assertions.assertThat(serviceResponse.status()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(serviceResponse.outcomeFile()).isPresent();
        Assertions.assertThat(serviceResponse.message()).isEmpty();
        LocalDateTime expectedTimeStamp = LocalDateTime.now(clock);
        verify(ipAddressValidation, times(1)).validate(eq(IP_ADDRESS));
        verify(fileHandler, times(1)).handle(eq(multipartFile), eq(REQUEST_ID));
        verify(requestDatabaseService, times(1)).saveRequest(eq(REQUEST_ID), eq(REQUEST_URL),
                eq(expectedTimeStamp), eq(HttpStatus.OK.value()), eq(IP_ADDRESS),
                eq("UK"), eq("IBM"), eq(100L));
    }

    @Test
    void assert_when_ip_blocked_response_is_FORBIDDEN() throws IOException {
        String message = "Blocked IP Address";
        when(ipAddressValidation.validate(any())).thenReturn(ValidationResult.blocked(IP_INFO, message));

        ServiceResponse serviceResponse = entryFileService.handle(IP_ADDRESS, REQUEST_URL, multipartFile);

        Assertions.assertThat(serviceResponse.status()).isEqualTo(HttpStatus.FORBIDDEN);
        Assertions.assertThat(serviceResponse.outcomeFile()).isEmpty();
        Assertions.assertThat(serviceResponse.message()).hasValue(message);
        LocalDateTime expectedTimeStamp = LocalDateTime.now(clock);
        verify(ipAddressValidation, times(1)).validate(eq(IP_ADDRESS));
        verify(fileHandler, never()).handle(any(), anyString());
        verify(requestDatabaseService, times(1)).saveRequest(eq(REQUEST_ID), eq(REQUEST_URL),
                eq(expectedTimeStamp), eq(HttpStatus.FORBIDDEN.value()), eq(IP_ADDRESS),
                eq("UK"), eq("IBM"), eq(100L));
    }

    @Test
    void assert_when_ip_lookup_fails_response_is_SERVICE_UNAVAILABLE() throws IOException {
        String message = "Error looking up IP Address";
        when(ipAddressValidation.validate(any())).thenReturn(ValidationResult.errored(message));

        ServiceResponse serviceResponse = entryFileService.handle(IP_ADDRESS, REQUEST_URL, multipartFile);

        Assertions.assertThat(serviceResponse.status()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        Assertions.assertThat(serviceResponse.outcomeFile()).isEmpty();
        Assertions.assertThat(serviceResponse.message()).hasValue(message);
        LocalDateTime expectedTimeStamp = LocalDateTime.now(clock);
        verify(ipAddressValidation, times(1)).validate(eq(IP_ADDRESS));
        verify(fileHandler, never()).handle(any(), anyString());
        verify(requestDatabaseService, times(1)).saveRequest(eq(REQUEST_ID), eq(REQUEST_URL),
                eq(expectedTimeStamp), eq(HttpStatus.SERVICE_UNAVAILABLE.value()), eq(IP_ADDRESS),
                isNull(), isNull(), eq(100L));
    }

    @Test
    void assert_when_ip_not_blocked_and_file_fails_validation_response_is_BAD_REQUEST() throws IOException {
        String message = "Error parsing text";
        when(ipAddressValidation.validate(any())).thenReturn(ValidationResult.success(IP_INFO));
        doThrow(new ValidationException(message, new IOException())).when(fileHandler).handle(any(), anyString());

        ServiceResponse serviceResponse = entryFileService.handle(IP_ADDRESS, REQUEST_URL, multipartFile);

        Assertions.assertThat(serviceResponse.status()).isEqualTo(HttpStatus.BAD_REQUEST);
        Assertions.assertThat(serviceResponse.outcomeFile()).isEmpty();
        Assertions.assertThat(serviceResponse.message()).hasValue(message);
        LocalDateTime expectedTimeStamp = LocalDateTime.now(clock);
        verify(ipAddressValidation, times(1)).validate(eq(IP_ADDRESS));
        verify(fileHandler, times(1)).handle(eq(multipartFile), eq(REQUEST_ID));
        verify(requestDatabaseService, times(1)).saveRequest(eq(REQUEST_ID), eq(REQUEST_URL),
                eq(expectedTimeStamp), eq(HttpStatus.BAD_REQUEST.value()), eq(IP_ADDRESS),
                eq("UK"), eq("IBM"), eq(100L));
    }

    @Test
    void assert_when_ip_not_blocked_and_file_fails_io_response_is_INTERNAL_SERVER_ERROR() throws IOException {
        when(ipAddressValidation.validate(any())).thenReturn(ValidationResult.success(IP_INFO));
        doThrow(new IOException()).when(fileHandler).handle(any(), anyString());

        ServiceResponse serviceResponse = entryFileService.handle(IP_ADDRESS, REQUEST_URL, multipartFile);

        Assertions.assertThat(serviceResponse.status()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        Assertions.assertThat(serviceResponse.outcomeFile()).isEmpty();
        Assertions.assertThat(serviceResponse.message()).isEmpty();
        LocalDateTime expectedTimeStamp = LocalDateTime.now(clock);
        verify(ipAddressValidation, times(1)).validate(eq(IP_ADDRESS));
        verify(fileHandler, times(1)).handle(eq(multipartFile), eq(REQUEST_ID));
        verify(requestDatabaseService, times(1)).saveRequest(eq(REQUEST_ID), eq(REQUEST_URL),
                eq(expectedTimeStamp), eq(HttpStatus.INTERNAL_SERVER_ERROR.value()), eq(IP_ADDRESS),
                eq("UK"), eq("IBM"), eq(100L));
    }
}