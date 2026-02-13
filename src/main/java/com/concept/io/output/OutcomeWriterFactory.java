package com.concept.io.output;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiFunction;

@Component
public class OutcomeWriterFactory<T> {

    private final Set<String> fieldsToWrite;
    private final String downloadDirectory;
    private final BiFunction<String, String, String> filenameFormatter;

    public OutcomeWriterFactory(@Value("${outcome-file.json.fields}") Set<String> fieldsToWrite,
                                @Value("${files.download.directory:files/download}") String downloadDirectory,
                                BiFunction<String, String, String> filenameFormatter) {
        this.fieldsToWrite = fieldsToWrite;
        this.downloadDirectory = downloadDirectory;
        this.filenameFormatter = filenameFormatter;
    }

    public OutcomeWriter<T> createJsonWriter(String requestId, Set<String> header) throws IOException {
        final Path path = Paths.get(downloadDirectory, filenameFormatter.apply(requestId, ".json"));
        final Set<String> fieldsToExclude = new LinkedHashSet<>(header);
        fieldsToExclude.removeAll(fieldsToWrite);
        return new JsonOutcomeWriter<>(path, fieldsToExclude);
    }
}
