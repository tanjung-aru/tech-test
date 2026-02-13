package com.concept.ip.validation;

import com.concept.ip.rest.IpInfo;
import com.concept.ip.rest.IpLookup;
import com.concept.ip.validation.validator.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class IpAddressValidation {

    private final IpLookup ipLookup;
    private final List<Validator> validators;

    public ValidationResult validate(String ipAddress) {
        try {
            final IpInfo ipInfo = ipLookup.getIpInfo(ipAddress);
            return validate(ipInfo, validators);
        } catch (Exception e) {
            log.error("Error looking up IP Address data for IP={}", ipAddress, e);
            return ValidationResult.errored("Error looking up IP Address");
        }
    }

    private static ValidationResult validate(IpInfo ipInfo, List<Validator> validators) {
        for (var validator : validators) {
            final Optional<String> optionalError = validator.validate(ipInfo);
            if (optionalError.isPresent()) {
                return ValidationResult.blocked(ipInfo, optionalError.get());
            }
        }
        return ValidationResult.success(ipInfo);
    }
}
