package com.concept.utils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

class FileUtilsTest {

    private final FileUtils fileUtils = new FileUtils();

    @TempDir
    Path tempDir;

    @Test
    void assert_that_file_is_written_to_disk() throws IOException {
        byte[] fileContents = "foobar".getBytes();
        InputStream inputStream = new ByteArrayInputStream(fileContents);
        Path path = tempDir.resolve("test-file.txt");

        fileUtils.saveFromInputStream(inputStream, path);

        Assertions.assertThat(Files.exists(path)).isTrue();
        Assertions.assertThat(Files.readAllBytes(path)).isEqualTo(fileContents);
    }
}