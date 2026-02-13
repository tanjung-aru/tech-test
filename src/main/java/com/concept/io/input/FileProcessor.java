package com.concept.io.input;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public interface FileProcessor {

    Path process(InputStream inputStream, String requestId) throws IOException;
}
