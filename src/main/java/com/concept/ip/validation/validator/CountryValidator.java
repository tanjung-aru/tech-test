package com.concept.ip.validation.validator;

import com.concept.ip.IpInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Optional;

@ConfigurationProperties(prefix = "client.country")
public record CountryValidator(List<String> blacklist) implements Validator {

    @Override
    public Optional<String> validate(IpInfo ipInfo) {

        if(blacklist.contains(ipInfo.countryCode())) {
            return Optional.of("Country is blacklisted: " + ipInfo.countryCode());
        }

        return Optional.empty();
    }
}
