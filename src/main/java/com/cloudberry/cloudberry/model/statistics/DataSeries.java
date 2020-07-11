package com.cloudberry.cloudberry.model.statistics;

import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class DataSeries {

    String seriesName;

    List<Map<String, Object>> data;

}
