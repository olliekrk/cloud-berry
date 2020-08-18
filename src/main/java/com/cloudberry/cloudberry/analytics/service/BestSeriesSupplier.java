package com.cloudberry.cloudberry.analytics.service;

import com.cloudberry.cloudberry.analytics.api.BestSeriesApi;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.OptimizationKind;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.Columns;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class BestSeriesSupplier implements BestSeriesApi {
    private final InfluxConfig influxConfig;
    private final InfluxDBClient influxClient;

    @Override
    public List<DataSeries> nBestSeriesForField(int n,
                                                String fieldName,
                                                OptimizationGoal optimizationGoal,
                                                OptimizationKind optimizationKind,
                                                @Nullable String bucketNameOpt,
                                                @Nullable String measurementName) {
        var bucketName = Optional.ofNullable(bucketNameOpt).orElseGet(influxConfig::getDefaultBucketName);
        var fieldRestriction = RestrictionsFactory.hasField(fieldName);
        var restrictions = measurementName != null ?
                Restrictions.and(RestrictionsFactory.measurement(measurementName), fieldRestriction) : fieldRestriction;

        var bestComputationsIdsQuery = switch (optimizationKind) {
            case FINAL_VALUE -> nBestComputationsByLastValueQuery(n, optimizationGoal, restrictions, bucketName);
            case AREA_UNDER_CURVE -> nBestComputationsByAreaQuery(n, optimizationGoal, restrictions, bucketName);
        };

        var bestComputationsIds = getComputationsIds(bestComputationsIdsQuery);

        return getComputationsSeries(bestComputationsIds, restrictions, bucketName);
    }

    private List<String> getComputationsIds(Flux fluxQuery) {
        return influxClient
                .getQueryApi()
                .query(fluxQuery.toString())
                .stream()
                .flatMap(table -> table.getRecords().stream())
                .map(record -> (String) record.getValueByKey(CommonTags.COMPUTATION_ID))
                .collect(Collectors.toList());
    }

    private List<DataSeries> getComputationsSeries(List<String> computationsIds,
                                                   Restrictions restrictions,
                                                   String bucketName) {
        var query = groupByComputationIdQuery(bucketName, restrictions)
                .filter(RestrictionsFactory.tagIn(CommonTags.COMPUTATION_ID, computationsIds));

        return influxClient
                .getQueryApi()
                .query(query.toString())
                .stream()
                .flatMap(table -> {
                    var records = table.getRecords();
                    if (records.isEmpty()) {
                        return Stream.of();
                    } else {
                        var recordsHead = records.get(0);
                        var computationId = (String) recordsHead.getValueByKey(CommonTags.COMPUTATION_ID);
                        return Stream.of(new DataSeries(computationId, ListSyntax.mapped(records, FluxRecord::getValues)));
                    }
                })
                .collect(Collectors.toList());
    }

    private static Flux nBestComputationsByAreaQuery(int n,
                                                     OptimizationGoal optimizationGoal,
                                                     Restrictions restrictions,
                                                     String bucketName) {
        var isDescendingBetter = optimizationGoal.equals(OptimizationGoal.MAX);
        return groupByComputationIdQuery(bucketName, restrictions)
                .keep(Set.of(Columns.TIME, Columns.VALUE, CommonTags.COMPUTATION_ID))
                .integral() // gets integral from each group
                .group() // ungroups the data back into 1 table
                .sort(Set.of(Columns.VALUE), isDescendingBetter)
                .limit(n)
                .keep(CommonTags.COMPUTATION_ID);
    }

    private static Flux nBestComputationsByLastValueQuery(int n,
                                                          OptimizationGoal optimizationGoal,
                                                          Restrictions restrictions,
                                                          String bucketName) {
        var isDescendingBetter = optimizationGoal.equals(OptimizationGoal.MAX);
        return groupByComputationIdQuery(bucketName, restrictions)
                .last() // gets last value from each group
                .keep(Set.of(Columns.VALUE, CommonTags.COMPUTATION_ID))
                .group() // ungroups the data back into 1 table
                .sort(Set.of(Columns.VALUE), isDescendingBetter)
                .limit(n)
                .keep(CommonTags.COMPUTATION_ID);
    }

    private static Flux groupByComputationIdQuery(String bucketName,
                                                  Restrictions restrictions) {
        return Flux
                .from(bucketName)
                .range(Instant.EPOCH)
                .filter(restrictions)
                .groupBy(CommonTags.COMPUTATION_ID);
    }

}
