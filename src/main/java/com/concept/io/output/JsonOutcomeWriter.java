package com.concept.io.output;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class JsonOutcomeWriter<T> implements OutcomeWriter<T> {

    private final ObjectMapper mapper;
    private final JsonGenerator generator;
    private final Path path;

    public JsonOutcomeWriter(Path path, Set<String> fieldsToExclude) throws IOException {
        this.path = path;
        createDirectories(path);
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setFilterProvider(new SimpleFilterProvider().addFilter(
                "dynamicFilter",
                SimpleBeanPropertyFilter.serializeAllExcept(fieldsToExclude)));
        generator = mapper.getFactory().createGenerator(Files.newBufferedWriter(path));
        generator.writeStartArray();
    }

    @Override
    public void write(T entry) throws IOException {
        mapper.writeValue(generator, entry);
    }

    @Override
    public void close() throws IOException {
        generator.close();
    }

    @Override
    public Path getPath() {
        return path;
    }

    private static void createDirectories(Path path) throws IOException {
        final Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }
}
