package com.concept.utils;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Component
public class FileUtils {

    public void saveFromInputStream(InputStream inputStream, Path path) throws IOException {

        Files.createDirectories(path.getParent());

        try (OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }
}
