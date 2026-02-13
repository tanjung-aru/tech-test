package com.concept.ip.validation.validator;

import com.concept.ip.rest.IpInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;
import java.util.Set;

@ConfigurationProperties(prefix = "client.country")
public record CountryValidator(Set<String> blacklist) implements Validator {

    @Override
    public Optional<String> validate(IpInfo ipInfo) {

        if(blacklist.contains(ipInfo.countryCode())) {
            return Optional.of("Country is blacklisted: " + ipInfo.countryCode());
        }

        return Optional.empty();
    }
}
