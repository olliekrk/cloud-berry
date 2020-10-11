package com.cloudberry.cloudberry.analytics.model;

import com.cloudberry.cloudberry.analytics.model.time.TimeRange;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.Value;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Value
public class DataSeries {
    private static final String TIME_FIELD_NAME = InfluxDefaults.Columns.TIME;

    String seriesName;

    List<Map<String, Object>> data;

    public boolean nonEmpty() {
        return !this.data.isEmpty();
    }

    public DataSeries renamed(String newSeriesName) {
        return new DataSeries(newSeriesName, data);
    }

    public static DataSeries empty(String seriesName) {
        return new DataSeries(seriesName, Collections.emptyList());
    }

    public List<Map<String, Object>> getDataSorted(String fieldName) {
        return data.stream()
                .sorted(Comparator.comparingDouble(dataPoint -> (Double) dataPoint.get(fieldName)))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getDataSortedByTime() {
        return data.stream()
                .sorted(Comparator.comparing(dataPoint -> (Instant) dataPoint.get(TIME_FIELD_NAME), Instant::compareTo))
                .collect(Collectors.toList());
    }

    public List<Instant> getTimePoints() {
        return data.stream()
                .flatMap(point -> Optional.ofNullable((Instant) point.get(TIME_FIELD_NAME)).stream())
                .collect(Collectors.toList());
    }

    public Optional<TimeRange> getTimeRange() {
        var timePoints = io.vavr.collection.List.ofAll(getTimePoints());
        return timePoints.isEmpty() ?
                Optional.empty() :
                Optional.of(new TimeRange(
                        timePoints.minBy(Instant::compareTo).get(),
                        timePoints.maxBy(Instant::compareTo).get()
                ));
    }

    public List<Tuple2<Instant, Double>> getFieldValueByTime(String fieldName, double defaultValue) {
        return getDataSortedByTime()
                .stream()
                .map(dataPoint -> Tuple.of(
                        (Instant) dataPoint.get(TIME_FIELD_NAME),
                        (double) dataPoint.getOrDefault(fieldName, defaultValue)
                ))
                .collect(Collectors.toList());
    }
}
