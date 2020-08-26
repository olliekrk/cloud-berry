package com.cloudberry.cloudberry.analytics.service;

import com.cloudberry.cloudberry.analytics.api.SeriesApi;
import com.cloudberry.cloudberry.analytics.api.ThresholdsApi;
import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.Thresholds;
import com.cloudberry.cloudberry.analytics.util.ComputationsRestrictionsFactory;
import com.cloudberry.cloudberry.analytics.util.FluxUtils;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.Columns;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ThresholdsSupplier implements ThresholdsApi {
    private final InfluxDBClient influxClient;
    private final SeriesApi seriesApi;

    @Override
    public List<DataSeries> thresholdsExceedingSeries(String fieldName,
                                                      Thresholds thresholds,
                                                      CriteriaMode mode,
                                                      InfluxQueryFields influxQueryFields) {
        var bucketName = influxQueryFields.getBucketName();
        var restrictions = influxQueryFields.getMeasurementNameOptional()
                .map(name -> ComputationsRestrictionsFactory.getFieldAndMeasurementNameRestrictions(fieldName, name))
                .orElse(ComputationsRestrictionsFactory.getFieldRestrictions(fieldName));

        var idsQuery = switch (mode) {
            case ANY -> anyValueOverThresholdQuery(thresholds, bucketName, restrictions);
            case FINAL -> finalValueOverThresholdQuery(thresholds, bucketName, restrictions);
            case AVERAGE -> averageValueOverThresholdQuery(thresholds, bucketName, restrictions);
        };

        var computationsIds = influxClient
                .getQueryApi()
                .query(idsQuery.toString())
                .stream()
                .flatMap(table -> FluxUtils.tableToSingleValue(table, r -> String.valueOf(r.getValue())))
                .filter(ObjectId::isValid)
                .map(ObjectId::new)
                .collect(Collectors.toList());

        return seriesApi.computationsSeries(fieldName, computationsIds, influxQueryFields);
    }

    private static Flux averageValueOverThresholdQuery(Thresholds thresholds,
                                                       String bucketName,
                                                       Restrictions restrictions) {
        return FluxUtils.epochQueryByComputationId(bucketName, restrictions)
                .mean()
                .filter(thresholdsToRestrictions(thresholds))
                .distinct(CommonTags.COMPUTATION_ID);

    }

    private static Flux anyValueOverThresholdQuery(Thresholds thresholds,
                                                   String bucketName,
                                                   Restrictions restrictions) {
        return FluxUtils.epochQuery(bucketName, restrictions)
                .filter(thresholdsToRestrictions(thresholds))
                .distinct(CommonTags.COMPUTATION_ID);
    }

    private static Flux finalValueOverThresholdQuery(Thresholds thresholds,
                                                     String bucketName,
                                                     Restrictions restrictions) {
        return FluxUtils.epochQueryByComputationId(bucketName, restrictions)
                .sort(new String[]{Columns.TIME})
                .last()
                .filter(thresholdsToRestrictions(thresholds))
                .distinct(CommonTags.COMPUTATION_ID);
    }

    private static Restrictions thresholdsToRestrictions(Thresholds thresholds) {
        return Restrictions.and(Stream.of(
                Optional.ofNullable(thresholds.getUpper()).map(upper -> Restrictions.value().greater(upper)).orElse(null),
                Optional.ofNullable(thresholds.getLower()).map(lower -> Restrictions.value().less(lower)).orElse(null)
        ).filter(Objects::nonNull).toArray(Restrictions[]::new));
    }

}
