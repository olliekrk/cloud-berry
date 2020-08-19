package com.cloudberry.cloudberry.analytics.service;

import com.cloudberry.cloudberry.analytics.api.MeanApi;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.rest.exceptions.FieldNotNumericException;
import com.cloudberry.cloudberry.service.api.StatisticsService;
import com.cloudberry.cloudberry.util.MathUtils;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Deprecated(since = "19.08.2020 - in favour of MovingAverageSupplier")
public class MeanSupplier implements MeanApi {

    private final static String MEAN_SERIES_NAME = "mean";

    public DataSeries mean(String fieldName,
                           List<DataSeries> series,
                           @Nullable String seriesSuffix) {
        var timeSeries = ListSyntax.mapped(series, s -> extractTimeAndfieldName(s, fieldName));
        var joinedSeries = ListSyntax.flatMapped(timeSeries, MeanSupplier::alignToStartAtZero);
        var intervalCount = ListSyntax.averageLengthCeil(timeSeries);
        var intervalEnd = joinedSeries.stream().map(Pair::getKey).max(Long::compareTo).orElse(0L);
        var intervalLength = intervalEnd.doubleValue() / intervalCount;

        var meanSeries = IntStream.range(0, intervalCount)
                .boxed()
                .flatMap(i -> {
                    var start = i * intervalLength;
                    var end = start + intervalLength;

                    var bucketData = joinedSeries.stream()
                            .dropWhile(dataPoint -> i != 0 && dataPoint.getKey() <= start)
                            .takeWhile(dataPoint -> dataPoint.getKey() <= end)
                            .filter(dataPoint -> dataPoint.getValue() != null)
                            .collect(Collectors.toList());

                    var bucketSize = bucketData.size();
                    if (bucketSize > 0) {
                        var bucketValues = ListSyntax.mapped(bucketData, Pair::getValue);
                        var bucketSum = bucketValues.stream().reduce(.0, Double::sum);
                        var bucketStd = MathUtils.standardDeviation(bucketValues);
                        var bucketMean = bucketSum / bucketSize;

                        Map<String, Object> meanPoint = Map.of(
                                InfluxDefaults.Columns.TIME, Instant.ofEpochMilli((long) ((start + end) / 2.)),
                                fieldName, bucketMean,
                                standardDeviationFieldName(fieldName), bucketStd
                        );
                        return Stream.of(meanPoint);
                    } else {
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());

        var seriesName = seriesSuffix == null ? MEAN_SERIES_NAME : String.format("%s_%s", MEAN_SERIES_NAME, seriesSuffix);
        return new DataSeries(seriesName, meanSeries);
    }

    private static String standardDeviationFieldName(String fieldName) {
        return String.format("%s_STD", fieldName);
    }

    private static List<Pair<Long, Double>> alignToStartAtZero(List<Pair<Long, Double>> series) {
        var minTime = series.stream().map(Map.Entry::getKey).min(Long::compareTo).orElse(0L);
        return ListSyntax.mapped(series, pair -> Pair.of(pair.getKey() - minTime, pair.getValue()));
    }

    private static List<Pair<Long, Double>> extractTimeAndfieldName(DataSeries series, String fieldName) {
        return series.getData()
                .stream()
                .filter(data -> data.containsKey(InfluxDefaults.Columns.TIME) && data.containsKey(fieldName))
                .map(data -> Try.of(() -> {
                    var timestamp = (Instant) data.get(InfluxDefaults.Columns.TIME);
                    var value = (Double) data.get(fieldName);
                    return Pair.of(timestamp.toEpochMilli(), value);
                }).getOrElseThrow(a -> new FieldNotNumericException(fieldName)))
                .collect(Collectors.toList());
    }

}
