package com.concept.io.output;

import com.concept.io.Entry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class JsonOutcomeWriterTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @TempDir
    Path tempDir;

    @Test
    void assert_that_all_fields_are_written() throws IOException {
        Entry entry = new Entry("uuid", "id", "name", "likes", "transport", new BigDecimal("1.0"), new BigDecimal("2.0"));
        Path tempFile = Files.createTempFile(tempDir, "test", ".json");

        try(JsonOutcomeWriter<Entry> jsonOutcomeWriter = new JsonOutcomeWriter<>(tempFile, Collections.emptySet())) {
            jsonOutcomeWriter.write(entry);

            Assertions.assertThat(jsonOutcomeWriter.getPath()).isEqualTo(tempFile);
        }

        List<Entry> entriesFromFile = objectMapper.readValue(tempFile.toFile(), new TypeReference<>(){});
        Assertions.assertThat(entriesFromFile.size()).isEqualTo(1);
        Assertions.assertThat(entriesFromFile.get(0)).isEqualTo(entry);
    }

    @Test
    void assert_that_only_selected_fields_are_written() throws IOException {
        Set<String> fieldsToExclude = Set.of("UUID", "ID", "Likes", "Avg Speed");
        Entry entry = new Entry("uuid", "id", "name", "likes", "transport", new BigDecimal("1.0"), new BigDecimal("2.0"));
        Entry expected = new Entry(null, null, "name", null, "transport", null, new BigDecimal("2.0"));
        Path tempFile = Files.createTempFile(tempDir, "test", ".json");

        try(JsonOutcomeWriter<Entry> jsonOutcomeWriter = new JsonOutcomeWriter<>(tempFile, fieldsToExclude)) {
            jsonOutcomeWriter.write(entry);

            Assertions.assertThat(jsonOutcomeWriter.getPath()).isEqualTo(tempFile);
        }

        List<Entry> entriesFromFile = objectMapper.readValue(tempFile.toFile(), new TypeReference<>(){});
        Assertions.assertThat(entriesFromFile.size()).isEqualTo(1);
        Assertions.assertThat(entriesFromFile.get(0)).isEqualTo(expected);
    }
}