package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataAccessor;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.rest.exceptions.FieldNotNumericException;
import com.cloudberry.cloudberry.util.MathUtils;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.influxdb.query.FluxRecord;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Map.of;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final static String MEAN_SERIES_NAME = "mean";
    private final MetadataService metadataService;
    private final InfluxDataAccessor influxDataAccessor;

    public List<DataSeries> getComputationsByIds(String comparedField,
                                                 @Nullable String measurementName,
                                                 @Nullable String bucketName,
                                                 List<ObjectId> computationIds,
                                                 boolean computeMean) {
        var computationSeries = computationIds
                .stream()
                .map(id -> getComputationSeries(measurementName, bucketName, id))
                .filter(DataSeries::nonEmpty)
                .collect(Collectors.toList());

        if (computeMean) {
            return ListSyntax.with(computationSeries, getMeanSeries(comparedField, computationSeries, MEAN_SERIES_NAME));
        } else {
            return computationSeries;
        }
    }

    public List<DataSeries> getComputationsByConfigurationId(String comparedField,
                                                             @Nullable String measurementName,
                                                             @Nullable String bucketName,
                                                             ObjectId configurationId,
                                                             boolean computeMean) {
        var computationIds = metadataService.findAllComputationIdsForConfiguration(configurationId);
        return getComputationsByIds(comparedField, measurementName, bucketName, computationIds, computeMean);
    }

    public List<DataSeries> getConfigurationsMeansByIds(String comparedField,
                                                        @Nullable String measurementName,
                                                        @Nullable String bucketName,
                                                        List<ObjectId> configurationIds) {
        return configurationIds
                .stream()
                .map(configurationId -> {
                    var series = getComputationsByConfigurationId(
                            comparedField,
                            measurementName,
                            bucketName,
                            configurationId,
                            false
                    );
                    var seriesName = String.format("%s_%s", MEAN_SERIES_NAME, configurationId.toHexString());
                    return getMeanSeries(comparedField, series, seriesName);
                })
                .collect(Collectors.toList());
    }

    public List<DataSeries> getConfigurationsMeansByExperimentName(String comparedField,
                                                                   @Nullable String measurementName,
                                                                   @Nullable String bucketName,
                                                                   String experimentName) {
        var configurationIds = metadataService.findAllConfigurationIdsForExperiment(experimentName);
        return getConfigurationsMeansByIds(comparedField, measurementName, bucketName, configurationIds);
    }

    private DataSeries getComputationSeries(@Nullable String measurementName,
                                            @Nullable String bucketName,
                                            ObjectId computationId) {
        var computationIdHex = computationId.toHexString();
        var records = influxDataAccessor.findData(
                bucketName,
                measurementName,
                Collections.emptyMap(),
                of(InfluxDefaults.CommonTags.COMPUTATION_ID, computationIdHex));

        return new DataSeries(computationIdHex, ListSyntax.mapped(records, FluxRecord::getValues));
    }

    private DataSeries getMeanSeries(String comparedField,
                                     List<DataSeries> series,
                                     String meanSeriesName) {
        var timeSeries = ListSyntax.mapped(series, s -> extractTimeAndComparedField(s, comparedField));
        var joinedSeries = ListSyntax.flatMapped(timeSeries, StatisticsService::alignToStartAtZero);
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
                                comparedField, bucketMean,
                                standardDeviationFieldName(comparedField), bucketStd
                        );
                        return Stream.of(meanPoint);
                    } else {
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());

        return new DataSeries(meanSeriesName, meanSeries);
    }

    private static String standardDeviationFieldName(String fieldName) {
        return String.format("%s_STD", fieldName);
    }

    private static List<Pair<Long, Double>> alignToStartAtZero(List<Pair<Long, Double>> series) {
        var minTime = series.stream().map(Map.Entry::getKey).min(Long::compareTo).orElse(0L);
        return ListSyntax.mapped(series, pair -> Pair.of(pair.getKey() - minTime, pair.getValue()));
    }

    private static List<Pair<Long, Double>> extractTimeAndComparedField(DataSeries series, String comparedField) {
        return series.getData()
                .stream()
                .filter(data -> data.containsKey(InfluxDefaults.Columns.TIME) && data.containsKey(comparedField))
                .map(data -> Try.of(() -> {
                    var timestamp = (Instant) data.get(InfluxDefaults.Columns.TIME);
                    var value = (Double) data.get(comparedField);
                    return Pair.of(timestamp.toEpochMilli(), value);
                }).getOrElseThrow(a -> new FieldNotNumericException(comparedField)))
                .collect(Collectors.toList());
    }
}
