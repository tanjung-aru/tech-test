package com.concept.ip.validation.validator;

import com.concept.ip.IpInfo;

import java.util.Optional;

public interface Validator {

    Optional<String> validate(IpInfo ipInfo);
}
