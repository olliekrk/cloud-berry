package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataAccessor;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.model.statistics.DataSeries;
import com.cloudberry.cloudberry.rest.exceptions.FieldNotNumericException;
import com.cloudberry.cloudberry.util.MathUtils;
import com.cloudberry.cloudberry.util.syntax.ListSyntax;
import com.influxdb.query.FluxRecord;
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
import java.util.Objects;
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

    public List<DataSeries> compareEvaluations(String comparedField,
                                               @Nullable String measurementName,
                                               @Nullable String bucketName,
                                               List<ObjectId> evaluationIds,
                                               boolean computeMean) {
        var evaluationSeries = evaluationIds
                .stream()
                .map(id -> findEvaluationData(measurementName, bucketName, id))
                .collect(Collectors.toList());

        if (computeMean) {
            return ListSyntax.with(evaluationSeries, getMeanSeries(comparedField, evaluationSeries, MEAN_SERIES_NAME));
        } else {
            return evaluationSeries;
        }
    }

    public List<DataSeries> compareEvaluationsForConfiguration(String comparedField,
                                                               @Nullable String measurementName,
                                                               @Nullable String bucketName,
                                                               ObjectId configurationId,
                                                               boolean computeMean) {
        var evaluationIds = metadataService.findAllEvaluationIdsForConfiguration(configurationId);
        return compareEvaluations(comparedField, measurementName, bucketName, evaluationIds, computeMean);
    }

    public List<DataSeries> compareConfigurations(String comparedField,
                                                  @Nullable String measurementName,
                                                  @Nullable String bucketName,
                                                  List<ObjectId> configurationIds) {
        return configurationIds
                .stream()
                .map(configurationId -> {
                    var series = compareEvaluationsForConfiguration(
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

    public List<DataSeries> compareConfigurationsForExperiment(String comparedField,
                                                               @Nullable String measurementName,
                                                               @Nullable String bucketName,
                                                               String experimentName) {
        var configurationIds = metadataService.findAllConfigurationIdsForExperiment(experimentName);
        return compareConfigurations(comparedField, measurementName, bucketName, configurationIds);
    }

    private DataSeries findEvaluationData(@Nullable String measurementName,
                                          @Nullable String bucketName,
                                          ObjectId evaluationId) {
        var evaluationIdHex = evaluationId.toHexString();
        var records = influxDataAccessor.findData(
                bucketName,
                measurementName,
                Collections.emptyMap(),
                of(InfluxDefaults.CommonTags.EVALUATION_ID, evaluationIdHex));

        return new DataSeries(evaluationIdHex, ListSyntax.mapped(records, FluxRecord::getValues));
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
                    var mid = Instant.ofEpochMilli((long) ((start + end) / 2.));

                    var bucket = joinedSeries.stream()
                            .dropWhile(dataPoint -> i != 0 && dataPoint.getKey() <= start)
                            .takeWhile(dataPoint -> dataPoint.getKey() <= end)
                            .collect(Collectors.toList());

                    var bucketSize = bucket.size();
                    if (bucketSize > 0) {
                        var bucketSum = bucket
                                .stream()
                                .map(Pair::getValue)
                                .filter(Objects::nonNull)
                                .reduce(.0, Double::sum);
                        var bucketMean = bucketSum / bucketSize; // average from bucket
                        var bucketStd = MathUtils.standardDeviation(
                                bucket.stream()
                                        .map(Pair::getValue)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList())
                        );
                        Map<String, Object> meanPoint = Map.of(
                                InfluxDefaults.Columns.TIME, mid,
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
                .map(data -> {
                    try {
                        var timestamp = (Instant) data.get(InfluxDefaults.Columns.TIME);
                        var value = (Double) data.get(comparedField);
                        return Pair.of(timestamp.toEpochMilli(), value);
                    } catch (ClassCastException e) {
                        throw new FieldNotNumericException(comparedField);
                    }
                })
                .collect(Collectors.toList());
    }
}
