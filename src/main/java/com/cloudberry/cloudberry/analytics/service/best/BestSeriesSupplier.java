package com.cloudberry.cloudberry.analytics.service.best;

import com.cloudberry.cloudberry.analytics.api.BestSeriesApi;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.OptionalQueryFields;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.service.ApiSupplier;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BestSeriesSupplier implements BestSeriesApi {
    private final InfluxConfig influxConfig;
    private final InfluxDBClient influxClient;

    @Override
    public List<DataSeries> nBestSeries(int n,
                                        String fieldName,
                                        Optimization optimization,
                                        OptionalQueryFields optionalQueryFields) {
        var bucketName = ApiSupplier.bucketNameOrDefault(optionalQueryFields.getBucketNameOptional(), influxConfig);
        var restrictions = ApiSupplier.getRestrictions(fieldName, optionalQueryFields.getMeasurementNameOptional());

        var bestComputationsIds = getBestComputationIds(n, optimization, bucketName, restrictions);

        return getComputationsSeries(bestComputationsIds, restrictions, bucketName);
    }

    private List<String> getBestComputationIds(
            int n,
            Optimization optimization,
            String bucketName,
            Restrictions restrictions) {
        var bestComputations = switch (optimization.getOptimizationKind()) {
            case FINAL_VALUE -> new BestComputationsByLastValue();
            case AREA_UNDER_CURVE -> new BestComputationsByArea();
        };

        final var optimizationGoal = optimization.getOptimizationGoal();
        var bestComputationsIdsQuery = bestComputations.getBest(n, optimizationGoal, restrictions, bucketName);

        return getComputationsIds(bestComputationsIdsQuery);
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
        var query = ApiSupplier.epochQueryByComputationId(bucketName, restrictions)
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

}
