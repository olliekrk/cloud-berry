package com.cloudberry.cloudberry.analytics.model;

import com.cloudberry.cloudberry.analytics.model.time.TimeRange;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.Value;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Value
@With
public class DataSeries {
    private static final String TIME_FIELD_NAME = InfluxDefaults.Columns.TIME;

    String seriesName;

    List<Map<String, Object>> data;

    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    public boolean nonEmpty() {
        return !isEmpty();
    }

    public DataSeries renamed(String newSeriesName) {
        return new DataSeries(newSeriesName, data);
    }

    public static DataSeries empty(String seriesName) {
        return new DataSeries(seriesName, Collections.emptyList());
    }

    public List<Map<String, Object>> getDataSortedBy(String fieldName) {
        return data.stream()
                .sorted(Comparator.comparingDouble(dataPoint -> (Double) dataPoint.get(fieldName)))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getDataSortedByTime() {
        return data.stream()
                .sorted(InfluxDefaults.Comparators.byTime)
                .collect(Collectors.toList());
    }

    public List<Instant> getTimePoints() {
        return data.stream()
                .flatMap(point -> Optional.ofNullable((Instant) point.get(TIME_FIELD_NAME)).stream())
                .collect(Collectors.toList());
    }

    public Optional<Instant> getStartTime() {
        return getTimePoints().stream().min(Instant::compareTo);
    }


    public Optional<Instant> getEndTime() {
        return getTimePoints().stream().min(Instant::compareTo);
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

    /**
     * Returns sorted tuples with first elements being timestamps from _time field and later being field values.
     */
    public List<Tuple2<Instant, Double>> getFieldValueByTime(String fieldName, double defaultValue) {
        return getDataSortedByTime()
                .stream()
                .map(dataPoint -> Tuple.of(
                        (Instant) dataPoint.get(TIME_FIELD_NAME),
                        (double) dataPoint.getOrDefault(fieldName, defaultValue)
                ))
                .collect(Collectors.toList());
    }

    public List<Tuple2<Instant, Double>> getFieldValueByTime(String fieldName) {
        return getDataSortedByTime()
                .stream()
                .flatMap(point -> {
                    try {
                        var time = (Instant) point.get(TIME_FIELD_NAME);
                        var valueOpt = Optional.ofNullable((Double) point.get(fieldName));
                        return valueOpt.stream().map(value -> Tuple.of(time, value));
                    } catch (Exception e) {
                        log.debug("Missing or invalid value for field: " + fieldName, e);
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());
    }

    public int getDataSize() {
        return this.data.size();
    }

}
