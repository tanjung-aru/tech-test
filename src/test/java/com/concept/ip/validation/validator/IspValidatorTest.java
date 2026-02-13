package com.concept.ip.validation.validator;

import com.concept.ip.rest.IpInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

class IspValidatorTest {

    private final IspValidator ispValidator = new IspValidator(Set.of("AWS", "GCP", "AZURE"));

    @Test
    void assert_that_whitelisted_isp_is_not_blocked() {

        Optional<String> message = ispValidator.validate(new IpInfo("", "IBM"));

        Assertions.assertThat(message).isEmpty();
    }

    @Test
    void assert_that_blacklisted_isp_is_blocked() {

        Optional<String> message = ispValidator.validate(new IpInfo("", "AWS"));

        Assertions.assertThat(message).isNotEmpty();
        Assertions.assertThat(message.get()).isEqualTo("ISP is blacklisted: AWS");
    }
}