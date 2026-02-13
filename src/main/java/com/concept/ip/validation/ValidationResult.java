package com.concept.ip.validation;

import com.concept.ip.IpInfo;

public sealed interface ValidationResult {

    IpInfo ipInfo();

    String message();

    record Success(IpInfo ipInfo, String message) implements ValidationResult {}

    record Blocked(IpInfo ipInfo, String message) implements ValidationResult {}

    record Error(IpInfo ipInfo, String message) implements ValidationResult {}

    static ValidationResult success(IpInfo ipInfo) {
        return new Success(ipInfo, "");
    }

    static ValidationResult failure(IpInfo ipInfo, String message) {
        return new Blocked(ipInfo, message);
    }

    static ValidationResult errored(String message) {
        return new Error(new IpInfo(null, null), message);
    }

    default boolean blocked() {
        return this instanceof Blocked;
    }

    default boolean errored() {
        return this instanceof Error;
    }

    default boolean succeeded() {
        return this instanceof Success;
    }
}
