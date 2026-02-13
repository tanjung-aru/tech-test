package com.concept.ip.validation.validator;

import com.concept.ip.IpInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Optional;

@ConfigurationProperties(prefix = "client.isp")
public record IspValidator(List<String> blacklist) implements Validator {

    @Override
    public Optional<String> validate(IpInfo ipInfo) {

        if(blacklist.contains(ipInfo.isp())) {
            return Optional.of("ISP is blacklisted: " + ipInfo.isp());
        }

        return Optional.empty();
    }
}
