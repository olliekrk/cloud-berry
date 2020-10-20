package com.cloudberry.cloudberry.analytics.service.thresholds;

import com.cloudberry.cloudberry.analytics.api.SeriesApi;
import com.cloudberry.cloudberry.analytics.api.ThresholdsApi;
import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.thresholds.Thresholds;
import com.cloudberry.cloudberry.analytics.model.thresholds.ThresholdsInfo;
import com.cloudberry.cloudberry.analytics.util.FluxUtils;
import com.cloudberry.cloudberry.analytics.util.computation.ComputationsRestrictionsFactory;
import com.cloudberry.cloudberry.common.syntax.CollectionSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.Columns;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.google.common.collect.Streams;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThresholdsSupplier implements ThresholdsApi {
    private final InfluxDBClient influxClient;
    private final SeriesApi seriesApi;

    @Override
    public List<DataSeries> thresholdsExceedingSeries(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields
    ) {
        var restrictions = RestrictionsFactory.everyRestriction(CollectionSyntax.flatten(List.of(
                influxQueryFields.getMeasurementNameOptional().map(RestrictionsFactory::measurement),
                Optional.of(fieldName).map(RestrictionsFactory::hasField)
        )));

        return thresholdExceedingSeriesWithRestrictions(fieldName, thresholds, mode, influxQueryFields, restrictions);
    }

    @Override
    public List<DataSeries> thresholdsExceedingSeriesFrom(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields,
            List<ObjectId> computationIds
    ) {
        var restrictions = RestrictionsFactory.everyRestriction(CollectionSyntax.flatten(List.of(
                influxQueryFields.getMeasurementNameOptional().map(RestrictionsFactory::measurement),
                Optional.of(fieldName).map(RestrictionsFactory::hasField),
                Optional.of(computationIds).map(ComputationsRestrictionsFactory::computationIdIn)
        )));

        return thresholdExceedingSeriesWithRestrictions(fieldName, thresholds, mode, influxQueryFields, restrictions);
    }

    @Override
    public List<DataSeries> thresholdsExceedingSeriesFrom(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            Map<ObjectId, DataSeries> dataSeries
    ) {
        Function<DataSeries, List<Double>> valuesToCheckForAny = series ->
                series.getDataSortedByTime()
                        .stream()
                        .flatMap(data -> Optional.ofNullable(data.get(fieldName)).map(o -> (double) o).stream())
                        .collect(Collectors.toList());

        Function<DataSeries, List<Double>> valuesToCheckForFinal = series ->
                Streams.findLast(series.getDataSortedByTime().stream())
                        .map(data -> (double) data.get(fieldName))
                        .stream()
                        .collect(Collectors.toList());

        Function<DataSeries, List<Double>> valuesToCheckForAverage = series -> {
            var stats = series.getDataSortedByTime()
                    .stream()
                    .flatMap(data -> Optional.ofNullable(data.get(fieldName)).map(o -> (double) o).stream())
                    .collect(Collectors.summarizingDouble(Double::doubleValue));
            return stats.getCount() == 0 ? List.<Double>of() : List.of(stats.getAverage());
        };

        var valuesToCheckExtractor = switch (mode) {
            case ANY -> valuesToCheckForAny;
            case FINAL -> valuesToCheckForFinal;
            case AVERAGE -> valuesToCheckForAverage;
        };

        var exceedingIds = dataSeries.entrySet().stream()
                .map(entry -> {
                    try {
                        return Tuple.of(entry.getKey(), valuesToCheckExtractor.apply(entry.getValue()));
                    } catch (Exception e) {
                        log.info("Failure when checking the thresholds for series and field: " + fieldName, e);
                        return Tuple.of(entry.getKey(), List.<Double>of());
                    }
                })
                .filter(tuple -> tuple._2.stream().anyMatch(value -> isOverOrUnderThreshold(value, thresholds)))
                .map(Tuple2::_1)
                .collect(Collectors.toSet());

        return exceedingIds.stream().map(dataSeries::get).collect(Collectors.toList());
    }

    @Override
    public List<DataSeries> thresholdsExceedingSeriesRelatively(
            String fieldName,
            ThresholdsInfo thresholdsInfo,
            CriteriaMode mode,
            Collection<DataSeries> dataSeries,
            DataSeries relativeSeries
    ) {
        return ThresholdsInMemoryOps
                .thresholdsExceedingSeriesRelatively(fieldName, thresholdsInfo, mode, dataSeries, relativeSeries);
    }

    private List<DataSeries> thresholdExceedingSeriesWithRestrictions(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields,
            Restrictions restrictions
    ) {
        var bucketName = influxQueryFields.getBucketName();
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

    private static Flux averageValueOverThresholdQuery(
            Thresholds thresholds,
            String bucketName,
            Restrictions restrictions
    ) {
        return FluxUtils.epochQueryByComputationId(bucketName, restrictions)
                .mean()
                .filter(thresholdsToRestrictions(thresholds))
                .distinct(CommonTags.COMPUTATION_ID);

    }

    private static Flux anyValueOverThresholdQuery(
            Thresholds thresholds,
            String bucketName,
            Restrictions restrictions
    ) {
        return FluxUtils.epochQuery(bucketName, restrictions)
                .filter(thresholdsToRestrictions(thresholds))
                .distinct(CommonTags.COMPUTATION_ID);
    }

    private static Flux finalValueOverThresholdQuery(
            Thresholds thresholds,
            String bucketName,
            Restrictions restrictions
    ) {
        return FluxUtils.epochQueryByComputationId(bucketName, restrictions)
                .sort(new String[]{Columns.TIME})
                .last()
                .filter(thresholdsToRestrictions(thresholds))
                .distinct(CommonTags.COMPUTATION_ID);
    }

    private static Restrictions thresholdsToRestrictions(Thresholds thresholds) {
        return Restrictions.and(Stream.of(
                Optional.ofNullable(thresholds.getUpper()).map(upper -> Restrictions.value().greater(upper))
                        .orElse(null),
                Optional.ofNullable(thresholds.getLower()).map(lower -> Restrictions.value().less(lower)).orElse(null)
        ).filter(Objects::nonNull).toArray(Restrictions[]::new));
    }

    private static boolean isOverOrUnderThreshold(double value, Thresholds thresholds) {
        return Optional.ofNullable(thresholds.getUpper()).filter(upper -> value > upper).isPresent() ||
                Optional.ofNullable(thresholds.getLower()).filter(lower -> value < lower).isPresent();
    }

}
