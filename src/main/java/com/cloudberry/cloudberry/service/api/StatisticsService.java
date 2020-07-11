package com.cloudberry.cloudberry.service.api;

import com.cloudberry.cloudberry.db.influx.InfluxCommonTags;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.service.InfluxDataAccessor;
import com.cloudberry.cloudberry.db.mongo.service.MetadataService;
import com.cloudberry.cloudberry.model.statistics.DataSeries;
import com.cloudberry.cloudberry.rest.exceptions.FieldNotNumericException;
import com.cloudberry.cloudberry.util.syntax.ListSyntax;
import com.influxdb.query.FluxRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Map.of;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final static String MEAN_SERIES_NAME = "_mean";
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

        return computeMean ?
                ListSyntax.with(evaluationSeries, getMeanSeries(comparedField, evaluationSeries)) :
                evaluationSeries;
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
                .map(configurationId -> compareEvaluationsForConfiguration(
                        comparedField,
                        measurementName,
                        bucketName,
                        configurationId,
                        false
                ))
                .map(series -> getMeanSeries(comparedField, series))
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
                of(InfluxCommonTags.EVALUATION_ID, evaluationIdHex));

        return new DataSeries(evaluationIdHex, ListSyntax.mapped(records, FluxRecord::getValues));
    }

    private DataSeries getMeanSeries(String comparedField, List<DataSeries> series) {
        var timeSeries = series.stream()
                .map(DataSeries::getData)
                .map(dataPoints -> dataPoints
                        .stream()
                        .filter(data -> data.containsKey(InfluxDefaults.Columns.TIME) && data.containsKey(comparedField))
                        .map(data -> {
                            try {
                                var timestamp = (double) data.get(InfluxDefaults.Columns.TIME);
                                var value = (double) data.get(comparedField);
                                return Pair.of(timestamp, value);
                            } catch (ClassCastException e) {
                                throw new FieldNotNumericException(comparedField);
                            }
                        })
                        .collect(Collectors.toList())
                ).collect(Collectors.toList());

        var joinedSeries = timeSeries
                .stream()
                .flatMap(rawSeries -> {
                    var minTime = rawSeries.stream().map(Map.Entry::getKey).min(Double::compareTo).orElse(.0);
                    return rawSeries.stream().map(pair -> Pair.of(pair.getKey() - minTime, pair.getValue()));
                })
                .collect(Collectors.toList());

        var intervalCount = (int) Math.ceil(
                timeSeries.stream()
                        .map(List::size)
                        .collect(Collectors.averagingInt(i -> i))
        );
        var intervalGap = joinedSeries.stream().map(Pair::getKey).max(Double::compareTo).orElse(.0);
        var intervalLength = intervalGap / intervalCount;

        var meanSeries = IntStream.range(0, intervalCount)
                .boxed()
                .map(i -> {
                    var start = i * intervalLength;
                    var mid = start + intervalLength * .5;
                    var end = start + intervalLength;

                    var bucket = joinedSeries.stream()
                            .dropWhile(dataPoint -> i != 0 && dataPoint.getLeft() <= start)
                            .takeWhile(dataPoint -> dataPoint.getLeft() <= end)
                            .collect(Collectors.toList());

                    var bucketSum = bucket.stream().map(Pair::getValue).reduce(.0, Double::sum);
                    var bucketMean = bucketSum / bucket.size(); // average from bucket
                    return Map.of(
                            InfluxDefaults.Columns.TIME, mid,
                            InfluxDefaults.Columns.VALUE, (Object) bucketMean
                    );
                })
                .collect(Collectors.toList());


        return new DataSeries(comparedField + MEAN_SERIES_NAME, meanSeries);
    }
}
