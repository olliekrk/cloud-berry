package com.cloudberry.cloudberry.db.common.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import org.springframework.lang.Nullable;

import java.util.Map;
import java.util.Optional;


@Value
public class ImportDetails {
    Map<String, String> headersKeys; // [WH] -> [W], etc.
    Map<String, String> headersMeasurements; // [W] -> workplace_log, etc.

    @JsonCreator
    public ImportDetails(@Nullable @JsonProperty("headersKeys") Map<String, String> headersKeys,
                         @Nullable @JsonProperty("headersMeasurements") Map<String, String> headersMeasurements) {
        this.headersKeys = Optional.ofNullable(headersKeys).orElse(Map.of());
        this.headersMeasurements = Optional.ofNullable(headersMeasurements).orElse(Map.of());
    }
}
