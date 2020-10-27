package com.cloudberry.cloudberry.analytics.service.average.moving;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.service.util.time.IntervalOps;
import com.cloudberry.cloudberry.analytics.service.util.time.TimeShiftOps;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class MovingAverageInMemoryOps {
    public static final String STDDEV_KEY = "stddev";
    public static final String AVERAGE_SERIES_NAME = "average";

    /**
     * @param useTimeShift         computes average series as if all series had the same starting point
     *                             (being start of first starting series)
     * @param skipIncompletePoints do not add point to result series if bucket did not contain at least 1 point from
     *                             each series
     */
    public static DataSeries movingAverageSeries(
            List<DataSeries> series,
            String fieldName,
            boolean useTimeShift,
            boolean skipIncompletePoints
    ) {
        if (useTimeShift) {
            series = TimeShiftOps.timeShiftIfPossible(series);
        }

        var allSeriesNames = series.stream().map(DataSeries::getSeriesName).collect(Collectors.toSet());
        var fieldData = flatFieldDataSortedByTime(series, fieldName);
        var firstIntervalStart = TimeShiftOps.minStartTime(series);
        var bucketDuration = IntervalOps.suitableMovingBucketDuration(series);

        var averageSeriesData = fieldData.stream()
                .collect(Collectors.groupingBy(point -> {
                    // grouping points by number of interval point belongs to
                    var time = point._1;
                    var durationSinceStart = Duration.between(firstIntervalStart, time);
                    return durationSinceStart.dividedBy(bucketDuration);
                }))
                .entrySet()
                .stream()
                .flatMap(entry -> {
                    var intervalNumber = entry.getKey();
                    var pointsInBucket = entry.getValue();
                    if (pointsInBucket.isEmpty()) {
                        return Stream.empty();
                    }

                    if (skipIncompletePoints) {
                        var seriesNames = pointsInBucket.stream()
                                .map(Tuple3::_2)
                                .collect(Collectors.toSet());
                        if (!seriesNames.containsAll(allSeriesNames)) {
                            return Stream.empty();
                        }
                    }

                    var bucketTime = Instant.ofEpochMilli((long) (bucketDuration.toMillis() * (intervalNumber + 0.5)));
                    var bucketSummary = pointsInBucket.stream().collect(Collectors.summarizingDouble(Tuple3::_3));
                    var bucketAvg = bucketSummary.getAverage();
                    var bucketStd = Math.sqrt(
                            pointsInBucket.stream()
                                    .map(Tuple3::_3)
                                    .collect(Collectors.averagingDouble(v -> Math.pow(v - bucketAvg, 2)))
                    );

                    return Stream.of(Map.<String, Object>of(
                            InfluxDefaults.Columns.TIME, bucketTime,
                            fieldName, bucketAvg,
                            STDDEV_KEY, bucketStd
                    ));
                })
                .collect(Collectors.toList());

        return new DataSeries(AVERAGE_SERIES_NAME, averageSeriesData);
    }

    /**
     * @return list of tuples, each containing (point timestamp, series name, point field value)
     */
    private static List<Tuple3<Instant, String, Double>> flatFieldDataSortedByTime(
            List<DataSeries> series, String fieldName
    ) {
        return series.stream()
                .flatMap(s -> {
                    var seriesName = s.getSeriesName();
                    return s.getData().stream().flatMap(point -> {
                        var time = Optional.ofNullable((Instant) point.get(InfluxDefaults.Columns.TIME));
                        var value = Optional.ofNullable((Double) point.get(fieldName));
                        if (time.isEmpty() || value.isEmpty()) {
                            log.debug("Data point ignored (missing time or %s)".formatted(fieldName));
                            return Stream.empty();
                        } else {
                            return Stream.of(Tuple.of(time.get(), seriesName, value.get()));
                        }
                    });
                })
                .sorted(Comparator.comparing(Tuple3::_1))
                .collect(Collectors.toList());
    }

}
