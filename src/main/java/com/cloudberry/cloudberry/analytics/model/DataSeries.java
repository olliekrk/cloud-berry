package com.cloudberry.cloudberry.analytics.model;

import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class DataSeries {

    String seriesName;

    List<Map<String, Object>> data;


    public boolean nonEmpty() {
        return !this.data.isEmpty();
    }

}
