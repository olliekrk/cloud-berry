package com.cloudberry.cloudberry.model.statistics;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Map;

public class DataSeriesPoint {
    @JsonValue
    List<Map<String, Object>> data;
}
