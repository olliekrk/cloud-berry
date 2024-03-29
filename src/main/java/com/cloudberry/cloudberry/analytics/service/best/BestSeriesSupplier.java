package com.cloudberry.cloudberry.analytics.service.best;

import com.cloudberry.cloudberry.analytics.api.BestSeriesApi;
import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.filters.DataFilters;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.util.FluxUtils;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BestSeriesSupplier implements BestSeriesApi {
    private final InfluxDBClient influxClient;

    @Override
    public List<DataSeries> nBestSeries(
            int n,
            String fieldName,
            Optimization optimization,
            InfluxQueryFields influxQueryFields,
            DataFilters dataFilters
    ) {
        var bucketName = influxQueryFields.getBucketName();
        var restrictions = RestrictionsFactory.combine(fieldName, dataFilters, influxQueryFields);

        return getBestComputations(n, optimization, bucketName, restrictions);
    }

    @Override
    public List<DataSeries> nBestSeriesFrom(
            int n,
            String fieldName,
            Optimization optimization,
            InfluxQueryFields influxQueryFields,
            List<ObjectId> computationIds,
            DataFilters dataFilters
    ) {
        if (computationIds.isEmpty()) { return List.of(); }

        var bucketName = influxQueryFields.getBucketName();
        var restrictions = RestrictionsFactory.combine(fieldName, dataFilters, influxQueryFields, computationIds);

        return getBestComputations(n, optimization, bucketName, restrictions);
    }

    @Override
    public List<DataSeries> nBestSeriesFrom(
            int n,
            String fieldName,
            Optimization optimization,
            Collection<DataSeries> dataSeries
    ) {
        return BestSeriesInMemoryOps.nBestSeriesFrom(n, fieldName, optimization, dataSeries);
    }

    private List<String> getBestComputationIds(
            int n,
            Optimization optimization,
            String bucketName,
            Restrictions restrictions
    ) {
        var bestComputations = switch (optimization.getOptimizationKind()) {
            case FINAL_VALUE -> new BestComputationsByLastValue();
            case AREA_UNDER_CURVE -> new BestComputationsByArea();
        };

        final var optimizationGoal = optimization.getOptimizationGoal();
        var bestComputationsIdsQuery = bestComputations.getBest(n, optimizationGoal, restrictions, bucketName);

        return queryForComputationIds(bestComputationsIdsQuery);
    }

    private List<DataSeries> getBestComputations(
            int n,
            Optimization optimization,
            String bucketName,
            Restrictions restrictions
    ) {
        var ids = getBestComputationIds(n, optimization, bucketName, restrictions);
        if (ids.isEmpty()) {return List.of();}
        return queryForComputations(ids, restrictions, bucketName);
    }

    private List<String> queryForComputationIds(Flux fluxQuery) {
        return influxClient
                .getQueryApi()
                .query(fluxQuery.toString())
                .stream()
                .flatMap(table -> table.getRecords().stream())
                .map(record -> (String) record.getValueByKey(CommonTags.COMPUTATION_ID))
                .collect(Collectors.toList());
    }

    private List<DataSeries> queryForComputations(
            List<String> computationsIds,
            Restrictions restrictions,
            String bucketName
    ) {
        var query = FluxUtils.epochQueryByComputationId(bucketName, restrictions)
                .filter(RestrictionsFactory.tagIn(CommonTags.COMPUTATION_ID, computationsIds))
                .pivot(
                        Set.of(CommonTags.COMPUTATION_ID, InfluxDefaults.Columns.TIME),
                        Set.of(InfluxDefaults.Columns.FIELD),
                        InfluxDefaults.Columns.VALUE
                )
                .groupBy(List.of(CommonTags.COMPUTATION_ID))
                .sort(List.of(InfluxDefaults.Columns.TIME));

        return influxClient
                .getQueryApi()
                .query(query.toString())
                .stream()
                .map(FluxUtils::tableToComputationSeries)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

}
