package com.cloudberry.cloudberry.analytics.model;

import lombok.Value;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Value
public class DataSeries {

    String seriesName;

    List<Map<String, Object>> data;

    public boolean nonEmpty() {
        return !this.data.isEmpty();
    }

    public DataSeries renamed(String newSeriesName){
        return new DataSeries(newSeriesName, data);
    }

    public static DataSeries empty(String seriesName){
        return new DataSeries(seriesName, Collections.emptyList());
    }

}
