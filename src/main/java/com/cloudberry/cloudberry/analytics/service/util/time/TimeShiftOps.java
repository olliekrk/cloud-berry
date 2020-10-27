package com.cloudberry.cloudberry.analytics.service.util.time;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class TimeShiftOps {
    private static final String TIME_KEY = InfluxDefaults.Columns.TIME;
    private static final Comparator<Map<String, Object>> TIME_COMPARATOR = InfluxDefaults.Comparators.byTime;

    public static List<DataSeries> timeShiftIfPossible(
            List<DataSeries> series
    ) {
        var originalSeries = series;
        try {
            originalSeries = timeShift(series);
        } catch (Exception e) {
            log.warn("Requested time shift could not be performed", e);
        }
        return originalSeries;
    }

    public static Instant minStartTime(List<DataSeries> series) {
        return series.stream()
                .flatMap(s -> s.getStartTime().stream())
                .min(Instant::compareTo)
                .orElseThrow(() -> new IllegalArgumentException("Supplied series are missing time markers."));
    }


    public static Instant maxEndTime(List<DataSeries> series) {
        return series.stream()
                .flatMap(s -> s.getEndTime().stream())
                .max(Instant::compareTo)
                .orElseThrow(() -> new IllegalArgumentException("Supplied series are missing time markers."));
    }

    public static List<DataSeries> timeShift(List<DataSeries> originalSeries) {
        var startingTime = minStartTime(originalSeries);
        return originalSeries.stream()
                .flatMap(singleSeries -> singleSeries.getStartTime().stream()
                        .map(start -> {
                            // from every point in the original originalSeries subtract `shift` from _time field
                            var shift = Duration.between(startingTime, start);
                            var updatedData = singleSeries.getData().stream()
                                    .filter(point -> point.containsKey(TIME_KEY))
                                    .map(point -> {
                                        var pointTime = (Instant) point.get(TIME_KEY);
                                        var newPoint = new HashMap<>(point);
                                        newPoint.put(TIME_KEY, pointTime.minus(shift));
                                        return Map.copyOf(newPoint);
                                    })
                                    .sorted(TIME_COMPARATOR)
                                    .collect(Collectors.toList());

                            return singleSeries.withData(updatedData);
                        })
                )
                .collect(Collectors.toList());
    }

}
