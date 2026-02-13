package com.concept.service;

import org.springframework.http.HttpStatus;

import java.nio.file.Path;
import java.util.Optional;

public record ServiceResponse(HttpStatus status, Optional<Path> outcomePath, Optional<String> message) {

    public ServiceResponse(HttpStatus status, Optional<Path> outcomePath) {
        this(status, outcomePath, Optional.empty());
    }

    public ServiceResponse(HttpStatus status, String message) {
        this(status, Optional.empty(), Optional.of(message));
    }

    public ServiceResponse(HttpStatus status) {
        this(status, Optional.empty(), Optional.empty());
    }
}
