package com.concept.configuration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;

import static com.concept.configuration.Config.FORMATTER;

class ConfigTest {

    private final Config config = new Config();

    @Test
    void requestId_is_unique() {
        int count = 1000;
        HashSet<String> requestIdSet = new HashSet<>();
        for(int i = 0; i < count; i++) {
            requestIdSet.add(config.requestIdSupplier().get());
        }
        Assertions.assertThat(requestIdSet.size()).isEqualTo(count);
    }

    @Test
    void filenameFormatter_returns_expected_value() {
        String filename = config.filenameFormatter().apply("REQUEST_ID", ".json");
        Assertions.assertThat(filename).isEqualTo(LocalDateTime.now().format(FORMATTER) + "-REQUEST_ID.json");
    }
}