package com.concept.io.input;

import com.concept.io.Entry;
import com.concept.io.output.OutcomeWriter;
import com.concept.io.output.OutcomeWriterFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EntryFileProcessorTest {

    private static final String REQUEST_ID = "REQUEST-FOO123";
    private static final Path OUTCOME_PATH = Paths.get("OutcomeFile.json");

    @Mock
    private OutcomeWriter<Entry> outcomeWriter;

    @Mock
    private OutcomeWriterFactory<Entry> writerFactory;

    @BeforeEach
    void setUp() throws IOException {
        when(writerFactory.createJsonWriter(anyString(), any())).thenReturn(outcomeWriter);
        when(outcomeWriter.getPath()).thenReturn(OUTCOME_PATH);
    }

    @ParameterizedTest
    @CsvSource({
            "10b2f1c9-3710-48e4-a5b8-7d40952525bc,JUDK3E,Mia Williams,Likes Peach,Rides A Scooter,2.9,36.8"
    })
    void assert_that_valid_csv_line_is_sent_to_outcome_writer(String uuid, String id, String name, String likes, String transport, String avgSpeed, String topSpeed) throws IOException {
        EntryFileProcessor entryFileProcessor = new EntryFileProcessor(true, writerFactory);
        try(InputStream inputStream = CsvHelper.builder().uuid(uuid).id(id).name(name).likes(likes).transport(transport).avgSpeed(avgSpeed).topSpeed(topSpeed).toInputStream()) {

            Path outputPath = entryFileProcessor.process(inputStream, REQUEST_ID);

            verify(writerFactory, times(1)).createJsonWriter(REQUEST_ID, EntryFileProcessor.CSV_HEADER);
            verify(outcomeWriter, times(1)).write(new Entry(uuid, id, name, likes, transport, new BigDecimal(avgSpeed), new BigDecimal(topSpeed)));
            Assertions.assertThat(outputPath).isEqualTo(OUTCOME_PATH);
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "'',JUDK3E,Mia Williams,Likes Peach,Rides A Scooter,2.9,36.8",
            "10b2f1c9-3710-48e4-a5b8-7d40952525bc,'',Mia Williams,Likes Peach,Rides A Scooter,2.9,36.8",
            "10b2f1c9-3710-48e4-a5b8-7d40952525bc,JUDK3E,'',Likes Peach,Rides A Scooter,2.9,36.8",
            "10b2f1c9-3710-48e4-a5b8-7d40952525bc,JUDK3E,Mia Williams,'',Rides A Scooter,2.9,36.8",
            "10b2f1c9-3710-48e4-a5b8-7d40952525bc,JUDK3E,Mia Williams,Likes Peach,'',2.9,36.8",
            "10b2f1c9-3710-48e4-a5b8-7d40952525bc,JUDK3E,Mia Williams,Likes Peach,Rides A Scooter,'',36.8",
            "10b2f1c9-3710-48e4-a5b8-7d40952525bc,JUDK3E,Mia Williams,Likes Peach,Rides A Scooter,2.9,''",
    })
    void assert_that_invalid_csv_lines_fail_fast_when_validation_is_enabled(String uuid, String id, String name, String likes, String transport, String avgSpeed, String topSpeed) throws IOException {
        EntryFileProcessor entryFileProcessor = new EntryFileProcessor(true, writerFactory);
        try(InputStream inputStream = CsvHelper.builder().uuid(uuid).id(id).name(name).likes(likes).transport(transport).avgSpeed(avgSpeed).topSpeed(topSpeed).toInputStream()) {

            Assertions.assertThatExceptionOfType(IOException.class).isThrownBy(() -> entryFileProcessor.process(inputStream, REQUEST_ID));

            verify(writerFactory, times(1)).createJsonWriter(REQUEST_ID, EntryFileProcessor.CSV_HEADER);
            verify(outcomeWriter, never()).write(any());
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "'',JUDK3E,Mia Williams,Likes Peach,Rides A Scooter,2.9,36.8",
            "10b2f1c9-3710-48e4-a5b8-7d40952525bc,'',Mia Williams,Likes Peach,Rides A Scooter,2.9,36.8",
            "10b2f1c9-3710-48e4-a5b8-7d40952525bc,JUDK3E,'',Likes Peach,Rides A Scooter,2.9,36.8",
            "10b2f1c9-3710-48e4-a5b8-7d40952525bc,JUDK3E,Mia Williams,'',Rides A Scooter,2.9,36.8",
            "10b2f1c9-3710-48e4-a5b8-7d40952525bc,JUDK3E,Mia Williams,Likes Peach,'',2.9,36.8",
            "10b2f1c9-3710-48e4-a5b8-7d40952525bc,JUDK3E,Mia Williams,Likes Peach,Rides A Scooter,'',36.8",
            "10b2f1c9-3710-48e4-a5b8-7d40952525bc,JUDK3E,Mia Williams,Likes Peach,Rides A Scooter,2.9,''",
    })
    void assert_that_invalid_csv_lines_are_tolerated_when_validation_is_disabled(String uuid, String id, String name, String likes, String transport, String avgSpeed, String topSpeed) throws IOException {
        EntryFileProcessor entryFileProcessor = new EntryFileProcessor(false, writerFactory);
        try(InputStream inputStream = CsvHelper.builder().uuid(uuid).id(id).name(name).likes(likes).transport(transport).avgSpeed(avgSpeed).topSpeed(topSpeed).toInputStream()) {

            Path outputPath = entryFileProcessor.process(inputStream, REQUEST_ID);

            verify(writerFactory, times(1)).createJsonWriter(REQUEST_ID, EntryFileProcessor.CSV_HEADER);
            verify(outcomeWriter, times(1)).write(new Entry(uuid, id, name, likes, transport, avgSpeed.isBlank() ? null : new BigDecimal(avgSpeed), topSpeed.isBlank() ? null : new BigDecimal(topSpeed)));
            Assertions.assertThat(outputPath).isEqualTo(OUTCOME_PATH);
        }
    }

    static class CsvHelper {
        Map<String, String> map = new LinkedHashMap<>();

        static CsvHelper builder() {
            return new CsvHelper();
        }

        CsvHelper() {
            map.put("UUID", null);
            map.put("ID", null);
            map.put("NAME", null);
            map.put("LIKES", null);
            map.put("TRANSPORT", null);
            map.put("AVG_SPEED", null);
            map.put("TOP_SPEED", null);
        }

        CsvHelper uuid(String uuid) {
            map.put("UUID", uuid);
            return this;
        }

        CsvHelper id(String id) {
            map.put("ID", id);
            return this;
        }

        CsvHelper name(String name) {
            map.put("NAME", name);
            return this;
        }

        CsvHelper likes(String likes) {
            map.put("LIKES", likes);
            return this;
        }

        CsvHelper transport(String transport) {
            map.put("TRANSPORT", transport);
            return this;
        }

        CsvHelper avgSpeed(String avgSpeed) {
            map.put("AVG_SPEED", avgSpeed);
            return this;
        }

        CsvHelper topSpeed(String topSpeed) {
            map.put("TOP_SPEED", topSpeed);
            return this;
        }

        String toCsvLine() {
            return map.values().stream()
                    .map(o -> o == null ? "" : o)
                    .collect(Collectors.joining("|"));
        }

        InputStream toInputStream() {
            return new ByteArrayInputStream(toCsvLine().getBytes(StandardCharsets.UTF_8));
        }
    }
}