package com.concept.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Configuration
public class Config {

    final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Bean
    public Supplier<String> requestIdSupplier() {
        return () -> UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Bean
    public BiFunction<String, String, String> filenameFormatter() {
        return (requestId, extension) -> LocalDateTime.now().format(FORMATTER) + "-" + requestId + extension;
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
