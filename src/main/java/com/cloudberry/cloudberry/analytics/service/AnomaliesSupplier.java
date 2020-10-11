package com.cloudberry.cloudberry.analytics.service;

import com.cloudberry.cloudberry.analytics.api.AnomaliesApi;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.anomalies.AnomalyReport;
import com.cloudberry.cloudberry.analytics.util.FluxUtils;
import com.cloudberry.cloudberry.analytics.util.computation.ComputationsRestrictionsFactory;
import com.cloudberry.cloudberry.common.syntax.CollectionSyntax;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnomaliesSupplier implements AnomaliesApi {
    private final InfluxDBClient influxClient;

    @Override
    public List<AnomalyReport> getReportsForComputations(String fieldName,
                                                         List<ObjectId> computationsIds,
                                                         InfluxQueryFields influxQueryFields) {
        val restrictions = RestrictionsFactory.everyRestriction(CollectionSyntax.flatten(List.of(
                influxQueryFields.getMeasurementNameOptional().map(RestrictionsFactory::measurement),
                Optional.of(fieldName).map(RestrictionsFactory::hasField),
                Optional.of(computationsIds).map(ComputationsRestrictionsFactory::computationIdIn)
        )));
        val baseQuery = FluxUtils.epochQueryByComputationId(influxQueryFields.getBucketName(), restrictions);

        val stddev = getReportPart(baseQuery.stddev());
        val mean = getReportPart(baseQuery.mean());
        val spread = getReportPart(baseQuery.spread());
        val min = getReportPart(baseQuery.min());
        val max = getReportPart(baseQuery.max());
        val maxDiff = getReportPart(
                baseQuery.difference().map(FluxUtils.FluxMappers.absValue).max(),
                FluxUtils.Imports.math
        );

        return ListSyntax.mapped(computationsIds, id -> new AnomalyReport(
                id, stddev.get(id), mean.get(id), spread.get(id), min.get(id), max.get(id), maxDiff.get(id)
        ));
    }

    private Map<ObjectId, Double> getReportPart(Flux query) {
        return getReportPart(query, "");
    }

    private Map<ObjectId, Double> getReportPart(Flux query, String queryPrefix) {
        return influxClient.getQueryApi()
                .query(queryPrefix + query.toString())
                .stream()
                .flatMap(table -> FluxUtils.tableToSingleValue(table, record -> {
                    val computationId = new ObjectId((String) Objects.requireNonNull(record.getValueByKey(InfluxDefaults.CommonTags.COMPUTATION_ID)));
                    val value = (Double) record.getValue();
                    return Tuple.of(computationId, value);
                }))
                .collect(Collectors.toMap(Tuple2::_1, Tuple2::_2));
    }


}
