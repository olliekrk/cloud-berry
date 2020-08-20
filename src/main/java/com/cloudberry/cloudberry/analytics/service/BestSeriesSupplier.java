package com.cloudberry.cloudberry.analytics.service;

import com.cloudberry.cloudberry.analytics.api.BestSeriesApi;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.OptimizationKind;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.Columns;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BestSeriesSupplier extends ApiSupplier implements BestSeriesApi {
    private final InfluxConfig influxConfig;
    private final InfluxDBClient influxClient;

    @Override
    public List<DataSeries> nBestSeries(int n,
                                        String fieldName,
                                        OptimizationGoal optimizationGoal,
                                        OptimizationKind optimizationKind,
                                        @Nullable String measurementNameOpt,
                                        @Nullable String bucketNameOpt) {
        var bucketName = bucketNameOrDefault(bucketNameOpt, influxConfig);
        var fieldRestriction = RestrictionsFactory.hasField(fieldName);
        var restrictions = measurementNameOpt == null ? fieldRestriction :
                Restrictions.and(RestrictionsFactory.measurement(measurementNameOpt), fieldRestriction);

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
        var query = epochQueryByComputationId(bucketName, restrictions)
                .filter(RestrictionsFactory.tagIn(CommonTags.COMPUTATION_ID, computationsIds))
                .pivot(
                        Set.of(CommonTags.COMPUTATION_ID, InfluxDefaults.Columns.TIME),
                        Set.of(InfluxDefaults.Columns.FIELD),
                        InfluxDefaults.Columns.VALUE
                );

        return influxClient
                .getQueryApi()
                .query(query.toString())
                .stream()
                .map(ApiSupplier::tableToComputationSeries)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    private static Flux nBestComputationsByAreaQuery(int n,
                                                     OptimizationGoal optimizationGoal,
                                                     Restrictions restrictions,
                                                     String bucketName) {
        var isDescendingBetter = optimizationGoal.equals(OptimizationGoal.MAX);
        return epochQueryByComputationId(bucketName, restrictions)
                .keep(Set.of(Columns.TIME, Columns.VALUE, CommonTags.COMPUTATION_ID))
                .integral() // gets integral from each group
                .group() // ungroups the data back into 1 table
                .sort(Set.of(Columns.VALUE), isDescendingBetter)
                .limit(n)
                .keep(Set.of(CommonTags.COMPUTATION_ID));
    }

    private static Flux nBestComputationsByLastValueQuery(int n,
                                                          OptimizationGoal optimizationGoal,
                                                          Restrictions restrictions,
                                                          String bucketName) {
        var isDescendingBetter = optimizationGoal.equals(OptimizationGoal.MAX);
        return epochQueryByComputationId(bucketName, restrictions)
                .last() // gets last value from each group
                .keep(Set.of(Columns.VALUE, CommonTags.COMPUTATION_ID))
                .group() // ungroups the data back into 1 table
                .sort(Set.of(Columns.VALUE), isDescendingBetter)
                .limit(n)
                .keep(Set.of(CommonTags.COMPUTATION_ID));
    }

}
