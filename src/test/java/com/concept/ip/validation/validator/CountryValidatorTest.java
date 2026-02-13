package com.concept.ip.validation.validator;

import com.concept.ip.rest.IpInfo;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

class CountryValidatorTest {

    private final CountryValidator countryValidator = new CountryValidator(Set.of("CN", "ES", "US"));

    @Test
    void assert_that_whitelisted_country_is_not_blocked() {

        Optional<String> message = countryValidator.validate(new IpInfo("GB", ""));

        Assertions.assertThat(message).isEmpty();
    }

    @Test
    void assert_that_blacklisted_country_is_blocked() {

        Optional<String> message = countryValidator.validate(new IpInfo("US", ""));

        Assertions.assertThat(message).isNotEmpty();
        Assertions.assertThat(message.get()).isEqualTo("Country is blacklisted: US");
    }
}