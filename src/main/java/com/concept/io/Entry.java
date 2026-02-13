package com.concept.io;

import com.concept.utils.BigDecimalOptionalValidationDeserializer;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;

@JsonFilter("dynamicFilter")
public record Entry(
        @JsonProperty("UUID") String uuid,
        @JsonProperty("ID") String id,
        @JsonProperty("Name") String name,
        @JsonProperty("Likes") String likes,
        @JsonProperty("Transport") String transport,
        @JsonProperty("Avg Speed") @JsonDeserialize(using = BigDecimalOptionalValidationDeserializer.class) BigDecimal avgSpeed,
        @JsonProperty("Top Speed") @JsonDeserialize(using = BigDecimalOptionalValidationDeserializer.class) BigDecimal topSpeed) { }