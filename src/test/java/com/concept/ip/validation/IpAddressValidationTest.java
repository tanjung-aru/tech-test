package com.concept.ip.validation;

import com.concept.ip.rest.IpInfo;
import com.concept.ip.rest.IpLookup;
import com.concept.ip.validation.validator.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpAddressValidationTest {

    private static final String IP_ADDRESS = "1.2.3.4";

    @Mock
    private IpLookup ipLookup;

    @Mock
    private Validator validator1;

    @Mock
    private Validator validator2;

    @Test
    void assert_that_success_is_returned_when_all_validators_pass() throws IOException, InterruptedException {
        IpAddressValidation ipAddressValidation = new IpAddressValidation(ipLookup, List.of(validator1, validator2));
        IpInfo ipInfo = new IpInfo("US", "AWS");
        when(ipLookup.getIpInfo(eq(IP_ADDRESS))).thenReturn(ipInfo);
        when(validator1.validate(any())).thenReturn(Optional.empty());
        when(validator2.validate(any())).thenReturn(Optional.empty());

        ValidationResult validationResult = ipAddressValidation.validate(IP_ADDRESS);

        verify(validator1, times(1)).validate(eq(ipInfo));
        verify(validator2, times(1)).validate(eq(ipInfo));
        Assertions.assertThat(validationResult.succeeded()).isTrue();
        Assertions.assertThat(validationResult.ipInfo()).isEqualTo(ipInfo);
        Assertions.assertThat(validationResult.message()).isEmpty();
    }

    @Test
    void assert_that_blocked_is_returned_when_any_validator_fails() throws IOException, InterruptedException {
        String validationMessage = "Country Code blocked";
        IpAddressValidation ipAddressValidation = new IpAddressValidation(ipLookup, List.of(validator1, validator2));
        IpInfo ipInfo = new IpInfo("US", "AWS");
        when(ipLookup.getIpInfo(eq(IP_ADDRESS))).thenReturn(ipInfo);
        when(validator1.validate(any())).thenReturn(Optional.empty());
        when(validator2.validate(any())).thenReturn(Optional.of(validationMessage));

        ValidationResult validationResult = ipAddressValidation.validate(IP_ADDRESS);

        verify(validator1, times(1)).validate(eq(ipInfo));
        verify(validator2, times(1)).validate(eq(ipInfo));
        Assertions.assertThat(validationResult.blocked()).isTrue();
        Assertions.assertThat(validationResult.ipInfo()).isEqualTo(ipInfo);
        Assertions.assertThat(validationResult.message()).isEqualTo(validationMessage);
    }

    @Test
    void assert_that_error_is_returned_when_lookup_service_fails() throws IOException, InterruptedException {
        IpAddressValidation ipAddressValidation = new IpAddressValidation(ipLookup, List.of(validator1, validator2));
        doThrow(IOException.class).when(ipLookup).getIpInfo(eq(IP_ADDRESS));

        ValidationResult validationResult = ipAddressValidation.validate(IP_ADDRESS);

        verify(validator1, never()).validate(any());
        verify(validator2, never()).validate(any());
        Assertions.assertThat(validationResult.errored()).isTrue();
        Assertions.assertThat(validationResult.ipInfo()).isEqualTo(new IpInfo(null, null));
        Assertions.assertThat(validationResult.message()).isEqualTo("Error looking up IP Address");
    }
}