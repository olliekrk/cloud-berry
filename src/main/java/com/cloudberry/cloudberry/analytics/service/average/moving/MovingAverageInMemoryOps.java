package com.cloudberry.cloudberry.analytics.service.average.moving;

import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.basic.SeriesInfo;
import com.cloudberry.cloudberry.analytics.service.util.time.IntervalOps;
import com.cloudberry.cloudberry.analytics.service.util.time.TimeShiftOps;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public abstract class MovingAverageInMemoryOps {
    public static final String STDDEV_KEY = "stddev";
    public static final String AVERAGE_SERIES_NAME = "average";

    public static Optional<DataSeries> movingAverageSeries(
            List<DataSeries> series,
            String fieldName
    ) {
        if (series.size() == 1) {
            return Optional.of(singleAverageSeries(series.get(0), fieldName));
        }
        return movingAverageSeries(series, fieldName, true, true, true);
    }

    /**
     * @param useTimeShift         computes average series as if all series had the same starting point
     *                             (being start of first starting series)
     * @param skipIncompletePoints do not add point to result series if bucket did not contain at least 1 point from
     *                             each series
     */
    public static Optional<DataSeries> movingAverageSeries(
            List<DataSeries> series,
            String fieldName,
            boolean useTimeShift,
            boolean skipIncompletePoints,
            boolean useWeightedAverage
    ) {
        if (series.size() == 1) {
            return Optional.of(singleAverageSeries(series.get(0), fieldName));
        }
        if (useTimeShift) {
            series = TimeShiftOps.timeShiftIfPossible(series);
        }

        var firstIntervalStartOpt = TimeShiftOps.minStartTime(series);
        if (firstIntervalStartOpt.isEmpty()) {
            log.warn("Moving average could not be performed - no time markers in series");
            return Optional.empty();
        }

        var firstBucketStart = firstIntervalStartOpt.get();
        var allSeriesIds =
                series.stream().map(DataSeries::getSeriesInfo).map(SeriesInfo::getId).collect(Collectors.toSet());
        var bucketDuration = IntervalOps.suitableMovingBucketDuration(series);
        var fieldData = flatFieldDataSortedByTime(series, fieldName);

        var averageSeriesData = fieldData.stream()
                .collect(Collectors.groupingBy(point -> getBucketNumber(point, firstBucketStart, bucketDuration)))
                .entrySet()
                .stream()
                .flatMap(entry -> {
                    var bucketNumber = entry.getKey();
                    var bucketPoints = entry.getValue();
                    if (bucketPoints.isEmpty()) {
                        return Stream.empty();
                    }

                    if (skipIncompletePoints && !getUniqueSeriesIds(bucketPoints).containsAll(allSeriesIds)) {
                        return Stream.empty();
                    }

                    var bucketTime = getBucketTime(bucketNumber, firstBucketStart, bucketDuration);
                    var bucketAvgStd = getBucketAvgAndStd(bucketPoints, useWeightedAverage);

                    return Stream.of(Map.<String, Object>of(
                            InfluxDefaults.Columns.TIME, bucketTime,
                            fieldName, bucketAvgStd._1,
                            STDDEV_KEY, bucketAvgStd._2
                    ));
                })
                .sorted(Comparator.comparing(m -> (Instant) m.get(InfluxDefaults.Columns.TIME)))
                .collect(Collectors.toList());

        return Optional.of(new DataSeries(new SeriesInfo(AVERAGE_SERIES_NAME), averageSeriesData));
    }

    public static DataSeries singleAverageSeries(
            DataSeries series,
            String fieldName
    ) {
        var seriesData = series
                .getFieldValueByTime(fieldName)
                .stream()
                .map(tuple -> Map.<String, Object>of(
                        InfluxDefaults.Columns.TIME, tuple._1,
                        fieldName, tuple._2,
                        STDDEV_KEY, 0
                ))
                .collect(Collectors.toList());
        return new DataSeries(new SeriesInfo(AVERAGE_SERIES_NAME), seriesData);
    }

    /**
     * @return list of tuples, each containing (point timestamp, seriesInfo, point field value)
     */
    private static List<Tuple3<Instant, SeriesInfo, Double>> flatFieldDataSortedByTime(
            List<DataSeries> series,
            String fieldName
    ) {
        return series.stream()
                .flatMap(s -> {
                    var seriesInfo = s.getSeriesInfo();
                    return s.getData().stream().flatMap(point -> {
                        var time = Optional.ofNullable((Instant) point.get(InfluxDefaults.Columns.TIME));
                        var value = Optional.ofNullable((Double) point.get(fieldName));
                        if (time.isEmpty() || value.isEmpty()) {
                            log.debug("Data point ignored (missing time or {})", fieldName);
                            return Stream.empty();
                        } else {
                            return Stream.of(Tuple.of(time.get(), seriesInfo, value.get()));
                        }
                    });
                })
                .sorted(Comparator.comparing(Tuple3::_1))
                .collect(Collectors.toList());
    }

    private static Set<String> getUniqueSeriesIds(
            List<Tuple3<Instant, SeriesInfo, Double>> points
    ) {
        return points.stream().map(Tuple3::_2).map(SeriesInfo::getId).collect(Collectors.toSet());
    }

    private static long getBucketNumber(
            Tuple3<Instant, SeriesInfo, Double> point,
            Instant firstBucketStart,
            Duration bucketDuration
    ) {
        var time = point._1;
        var durationSinceStart = Duration.between(firstBucketStart, time);
        return bucketDuration.isZero() ? 0 : durationSinceStart.dividedBy(bucketDuration);
    }

    private static Instant getBucketTime(
            long bucketNumber,
            Instant firstBucketStart,
            Duration bucketDuration
    ) {
        return Instant
                .ofEpochMilli((long) (bucketDuration.toMillis() * (bucketNumber + 0.5)))
                .plusMillis(firstBucketStart.toEpochMilli());
    }

    private static Tuple2<Double, Double> getBucketAvgAndStd(
            List<Tuple3<Instant, SeriesInfo, Double>> points,
            boolean useWeightedAverage
    ) {
        return useWeightedAverage ? getBucketAvgAndStdWeighted(points) : getBucketAvgAndStdNonWeighted(points);
    }

    private static Tuple2<Double, Double> getBucketAvgAndStdWeighted(
            List<Tuple3<Instant, SeriesInfo, Double>> points
    ) {
        var pointsSize = points.size();
        var weightsForSeries = points.stream()
                .collect(Collectors.groupingBy(Tuple3::_2, Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> Tuple.of(entry.getKey(), 1. / entry.getValue()))
                .collect(Collectors.toMap(Tuple2::_1, Tuple2::_2));
        var weightsSum = weightsForSeries.size();

        var bucketAvg = points.stream()
                .mapToDouble(t -> weightsForSeries.get(t._2) * t._3)
                .sum() / weightsSum;

        // formula for weighted standard deviation
        // https://www.itl.nist.gov/div898/software/dataplot/refman2/ch2/weightsd.pdf
        var bucketStd = pointsSize <= 1 ? 0 : Math.sqrt(
                points.stream()
                        .mapToDouble(t -> {
                            var weight = 1. / weightsForSeries.get(t._2);
                            return weight * Math.pow(t._3 - bucketAvg, 2);
                        })
                        .sum() / ((pointsSize - 1.) / pointsSize * weightsSum)
        );

        return Tuple.of(bucketAvg, bucketStd);
    }

    private static Tuple2<Double, Double> getBucketAvgAndStdNonWeighted(
            List<Tuple3<Instant, SeriesInfo, Double>> points
    ) {
        var pointsSize = points.size();
        var bucketAvg = points.stream()
                .mapToDouble(Tuple3::_3)
                .average()
                .orElse(0);
        var bucketStd = pointsSize <= 1 ? 0 : Math.sqrt(
                points.stream()
                        .map(Tuple3::_3)
                        .mapToDouble(v -> Math.pow(v - bucketAvg, 2))
                        .sum() / (pointsSize - 1)
        );

        return Tuple.of(bucketAvg, bucketStd);
    }

}
