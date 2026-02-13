package com.concept.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.math.BigDecimal;

public class BigDecimalOptionalValidationDeserializer extends JsonDeserializer<BigDecimal> {

    @Value("${csv.validation.enabled:true}")
    boolean validationEnabled;

    @Override
    public BigDecimal deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        final String text = jsonParser.getText();
        try {
            if(text != null && !text.isBlank()) {
                return new BigDecimal(text);
            }
        } catch(NumberFormatException e) {
            if(validationEnabled) {
                throw e;
            }
        }
        return null;
    }
}
