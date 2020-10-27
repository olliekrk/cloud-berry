package com.cloudberry.cloudberry.analytics.model.basic;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Map;

public class DataSeriesPoint {
    @JsonValue
    List<Map<String, Object>> data;
}
