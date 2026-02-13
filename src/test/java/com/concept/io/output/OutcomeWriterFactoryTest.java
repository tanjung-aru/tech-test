package com.concept.io.output;

import com.concept.io.Entry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutcomeWriterFactoryTest {

    static final Set<String> CSV_HEADER = new LinkedHashSet<>(List.of("UUID", "ID", "Name", "Likes", "Transport", "Avg Speed", "Top Speed"));
    private static final String REQUEST_ID = "REQUEST-FOO123";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BiFunction<String, String, String> filenameFormatter;

    @TempDir
    Path tempDir;

    @Test
    void assert_that_json_writer_is_created_with_expected_fields() throws IOException {

        Entry entry = new Entry("uuid", "id", "name", "likes", "transport", new BigDecimal("1.0"), new BigDecimal("2.0"));
        Entry expected = new Entry(null, null, "name", null, "transport", null, new BigDecimal("2.0"));
        when(filenameFormatter.apply(any(), any())).thenReturn("OutcomeFile.json");
        OutcomeWriterFactory<Entry> outcomeWriterFactory = new OutcomeWriterFactory<>(Set.of("Name", "Transport", "Top Speed"), tempDir.toString(), filenameFormatter);

        Path path;
        try(OutcomeWriter<Entry> jsonWriter = outcomeWriterFactory.createJsonWriter(REQUEST_ID, CSV_HEADER)) {
            jsonWriter.write(entry);
            path = jsonWriter.getPath();
        }

        List<Entry> entriesFromFile = objectMapper.readValue(path.toFile(), new TypeReference<>(){});
        Assertions.assertThat(entriesFromFile.size()).isEqualTo(1);
        Assertions.assertThat(entriesFromFile.get(0)).isEqualTo(expected);
    }
}