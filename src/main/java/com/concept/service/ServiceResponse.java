package com.concept.service;

import org.springframework.http.HttpStatus;

import java.nio.file.Path;
import java.util.Optional;

public record ServiceResponse(HttpStatus status, Optional<Path> outcomeFile, Optional<String> message) {

    public ServiceResponse(HttpStatus status, Path outcomePath) {
        this(status, Optional.of(outcomePath), Optional.empty());
    }

    public ServiceResponse(HttpStatus status, String message) {
        this(status, Optional.empty(), Optional.of(message));
    }

    public ServiceResponse(HttpStatus status) {
        this(status, Optional.empty(), Optional.empty());
    }
}
