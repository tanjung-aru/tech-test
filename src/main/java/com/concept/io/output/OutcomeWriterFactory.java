package com.concept.io.output;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.BiFunction;

@Component
@RequiredArgsConstructor
public class OutcomeWriterFactory<T> {

    private final BiFunction<String, String, String> filenameFormatter;

    @Value("${files.download.directory:files/download}")
    private String downloadDirectory;

    public OutcomeWriter<T> createJsonWriter(String requestId, Set<String> fieldsToExclude) throws IOException {
        final Path path = Paths.get(downloadDirectory, filenameFormatter.apply(requestId, ".json"));
        return new JsonOutcomeWriter<>(path, fieldsToExclude);
    }
}
