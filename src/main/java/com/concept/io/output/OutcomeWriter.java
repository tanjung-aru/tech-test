package com.concept.io.output;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

public interface OutcomeWriter<T> extends Closeable {

    void write(T t) throws IOException;

    Path getPath();
}
