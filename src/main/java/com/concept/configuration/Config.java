package com.concept.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@Configuration
public class Config {

    @Bean
    public Supplier<String> requestIdSupplier() {
        return () -> UUID.randomUUID().toString().replaceAll("-", "");
    }

    @Bean
    public BiFunction<String, String, String> filenameFormatter() {
        final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
        return (requestId, ext) -> {
            final String date = LocalDateTime.now().format(FORMATTER);
            return date + "-" + requestId + ext;
        };
    }
}
