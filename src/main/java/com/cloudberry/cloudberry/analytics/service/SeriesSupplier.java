package com.cloudberry.cloudberry.analytics.service;

import com.cloudberry.cloudberry.analytics.api.SeriesApi;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.Columns;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeriesSupplier extends ApiSupplier implements SeriesApi {
    private final InfluxConfig influxConfig;
    private final InfluxDBClient influxClient;

    @Override
    public List<DataSeries> computationsSeries(List<ObjectId> computationsIds,
                                               @Nullable String measurementNameOpt,
                                               @Nullable String bucketNameOpt) {
        var bucketName = bucketNameOrDefault(bucketNameOpt, influxConfig);
        var tagRestriction = RestrictionsFactory
                .tagIn(CommonTags.COMPUTATION_ID, ListSyntax.mapped(computationsIds, ObjectId::toHexString));
        var restrictions = measurementNameOpt == null ?
                tagRestriction : Restrictions.and(RestrictionsFactory.measurement(measurementNameOpt), tagRestriction);

        var query = epochQuery(bucketName, restrictions)
                .pivot(
                        Set.of(CommonTags.COMPUTATION_ID, Columns.TIME),
                        Set.of(Columns.FIELD),
                        Columns.VALUE
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
