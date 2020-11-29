package com.cloudberry.cloudberry.parsing.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FieldTypes {

    private final Map<String, FieldType> mapping;

    @JsonCreator
    public FieldTypes(@JsonProperty("mapping") Map<String, FieldType> mapping) {
        this.mapping = mapping;
    }

    public static FieldTypes allStrings(List<String> rawFieldsParam) {
        var mapping = rawFieldsParam.stream().collect(Collectors.toMap(field -> field, __ -> FieldType.STRING));
        return new FieldTypes(mapping);
    }

    public FieldType getOrDefault(String field, FieldType defaultFieldType) {
        return mapping.getOrDefault(field, defaultFieldType);
    }

    public FieldType getOrDefault(String field) {
        return getOrDefault(field, FieldType.NUMBER);
    }

    public static FieldTypes empty() {
        return new FieldTypes(Map.of());
    }
}
