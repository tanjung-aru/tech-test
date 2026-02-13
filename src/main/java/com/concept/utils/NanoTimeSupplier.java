package com.concept.utils;

import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public class NanoTimeSupplier implements Supplier<Long> {

    @Override
    public Long get() {
        return System.nanoTime();
    }
}
