package com.concept.io.input;

import com.concept.io.Entry;
import com.concept.io.output.OutcomeWriterFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class EntryFileProcessor implements FileProcessor {

    static final Set<String> CSV_HEADER = new LinkedHashSet<>(List.of("UUID", "ID", "Name", "Likes", "Transport", "Avg Speed", "Top Speed"));
    private static final CsvMapper CSV_MAPPER;
    private static final CsvSchema CSV_SCHEMA;
    private final boolean validationEnabled;
    private final OutcomeWriterFactory<Entry> writerFactory;

    static {
        CSV_MAPPER = new CsvMapper();
        final CsvSchema.Builder builder = CsvSchema.builder();
        CSV_HEADER.forEach(builder::addColumn);
        CSV_SCHEMA = builder.setColumnSeparator('|').build();
    }

    public EntryFileProcessor(@Value("${csv.validation.enabled:true}") boolean validationEnabled, OutcomeWriterFactory<Entry> writerFactory) {
        this.validationEnabled = validationEnabled;
        this.writerFactory = writerFactory;
        if(validationEnabled) {
            CSV_MAPPER.enable(CsvParser.Feature.EMPTY_STRING_AS_NULL);
            CSV_MAPPER.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
            CSV_MAPPER.enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
            CSV_MAPPER.enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
        } else {
            CSV_MAPPER.disable(CsvParser.Feature.EMPTY_STRING_AS_NULL);
            CSV_MAPPER.disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
            CSV_MAPPER.disable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES);
            CSV_MAPPER.disable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
        }
        CSV_MAPPER.enable(CsvParser.Feature.SKIP_EMPTY_LINES); // Always skip empty lines.
    }

    @Override
    public Path process(InputStream inputStream, String requestId) throws IOException {
        try(var csvReader = CSV_MAPPER.readerFor(Entry.class).with(CSV_SCHEMA).createParser(inputStream);
            var outcomeWriter = writerFactory.createJsonWriter(requestId, CSV_HEADER)) {
            while (csvReader.nextToken() != null) {
                Entry entry;
                try {
                     entry = csvReader.readValueAs(Entry.class);
                } catch(IOException e) {
                    if(validationEnabled) {
                        final String errorMessage =
                                String.format("Error parsing text=\"%s\" on line=%s for requestId=%s",
                                csvReader.getText(), csvReader.currentLocation().getLineNr()-1, requestId);
                        throw new ValidationException(errorMessage, e); // Fail-fast on validation errors.
                    }
                    continue;
                }
                outcomeWriter.write(entry);
            }
            return outcomeWriter.getPath();
        }
    }
}
