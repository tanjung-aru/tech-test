package com.concept.ip.validation.validator;

import com.concept.ip.rest.IpInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Optional;
import java.util.Set;

@ConfigurationProperties(prefix = "client.isp")
public record IspValidator(Set<String> blacklist) implements Validator {

    @Override
    public Optional<String> validate(IpInfo ipInfo) {

        if(blacklist.contains(ipInfo.isp())) {
            return Optional.of("ISP is blacklisted: " + ipInfo.isp());
        }

        return Optional.empty();
    }
}
