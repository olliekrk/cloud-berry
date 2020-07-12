package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataAccessor;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.model.statistics.DataSeries;
import com.cloudberry.cloudberry.rest.exceptions.FieldNotNumericException;
import com.cloudberry.cloudberry.util.syntax.ListSyntax;
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

    public List<DataSeries> compareEvaluations(String comparedField,
                                               String measurementName,
                                               @Nullable String bucketName,
                                               List<ObjectId> evaluationIds,
                                               boolean computeMean) {
        var evaluationSeries = evaluationIds
                .stream()
                .map(id -> getEvaluationData(measurementName, bucketName, id))
                .collect(Collectors.toList());

        if (computeMean) {
            return ListSyntax.with(evaluationSeries, getMeanSeries(comparedField, evaluationSeries, MEAN_SERIES_NAME));
        } else {
            return evaluationSeries;
        }
    }

    public List<DataSeries> compareEvaluationsForConfiguration(String comparedField,
                                                               String measurementName,
                                                               @Nullable String bucketName,
                                                               ObjectId configurationId,
                                                               boolean computeMean) {
        var evaluationIds = metadataService.findAllEvaluationIdsForConfiguration(configurationId);
        return compareEvaluations(comparedField, measurementName, bucketName, evaluationIds, computeMean);
    }

    public List<DataSeries> compareConfigurations(String comparedField,
                                                  String measurementName,
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
                                                               String measurementName,
                                                               @Nullable String bucketName,
                                                               String experimentName) {
        var configurationIds = metadataService.findAllConfigurationIdsForExperiment(experimentName);
        return compareConfigurations(comparedField, measurementName, bucketName, configurationIds);
    }

    private DataSeries getEvaluationData(String measurementName,
                                         @Nullable String bucketName,
                                         ObjectId evaluationId) {
        var evaluationIdHex = evaluationId.toHexString();
        var records = influxDataAccessor.findMeasurements(
                bucketName,
                measurementName,
                Collections.emptyMap(),
                of(InfluxDefaults.CommonTags.EVALUATION_ID, evaluationIdHex));

        return new DataSeries(evaluationIdHex, ListSyntax.mapped(records, FluxRecord::getValues));
    }

    private DataSeries getMeanSeries(String comparedField,
                                     List<DataSeries> series,
                                     String meanSeriesName) {
        var timeSeries = series.stream()
                .map(DataSeries::getData)
                .map(dataPoints -> dataPoints
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
                        .collect(Collectors.toList())
                ).collect(Collectors.toList());

        var joinedSeries = timeSeries
                .stream()
                .flatMap(rawSeries -> {
                    var minTime = rawSeries.stream().map(Map.Entry::getKey).min(Long::compareTo).orElse(0L);
                    return rawSeries.stream().map(pair -> Pair.of(pair.getKey() - minTime, pair.getValue()));
                })
                .collect(Collectors.toList());

        var intervalCount = (int) Math.ceil(
                timeSeries.stream()
                        .map(List::size)
                        .collect(Collectors.averagingInt(i -> i))
        );
        var intervalGap = joinedSeries.stream().map(Pair::getKey).max(Long::compareTo).orElse(0L);
        var intervalLength = intervalGap.doubleValue() / intervalCount;

        var meanSeries = IntStream.range(0, intervalCount)
                .boxed()
                .flatMap(i -> {
                    var start = i * intervalLength;
                    var mid = Instant.ofEpochMilli((long) (start + intervalLength * .5));
                    var end = start + intervalLength;

                    var bucket = joinedSeries.stream()
                            .dropWhile(dataPoint -> i != 0 && dataPoint.getLeft() <= start)
                            .takeWhile(dataPoint -> dataPoint.getLeft() <= end)
                            .collect(Collectors.toList());

                    var bucketSize = bucket.size();
                    if (bucketSize > 0) {
                        var bucketSum = bucket.stream().map(Pair::getValue).reduce(.0, Double::sum);
                        var bucketMean = bucketSum / bucketSize; // average from bucket
                        var meanPoint = Map.of(
                                InfluxDefaults.Columns.TIME, mid,
                                comparedField, (Object) bucketMean
                        );
                        return Stream.of(meanPoint);
                    } else {
                        return Stream.empty();
                    }
                })
                .collect(Collectors.toList());


        return new DataSeries(meanSeriesName, meanSeries);
    }
}
