package com.concept.io.input;

import java.io.IOException;

public class ValidationException extends IOException {

    public ValidationException(String message, Exception cause) {
        super(message, cause);
    }
}
