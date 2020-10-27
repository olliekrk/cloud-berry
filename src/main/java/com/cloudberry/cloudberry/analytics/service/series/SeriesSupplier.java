package com.cloudberry.cloudberry.analytics.service.series;

import com.cloudberry.cloudberry.analytics.api.SeriesApi;
import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.util.FluxUtils;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.Columns;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeriesSupplier implements SeriesApi {
    private final InfluxDBClient influxClient;

    @Override
    public List<DataSeries> computationsSeries(
            String fieldName,
            List<ObjectId> computationsIds,
            InfluxQueryFields influxQueryFields
    ) {
        if (computationsIds.isEmpty()) {
            return Collections.emptyList();
        }

        var bucketName = influxQueryFields.getBucketName();
        var fieldRestriction = RestrictionsFactory.hasField(fieldName);
        var tagRestriction = RestrictionsFactory
                .tagIn(CommonTags.COMPUTATION_ID, ListSyntax.mapped(computationsIds, ObjectId::toHexString));
        final var necessaryRestrictions = Restrictions.and(fieldRestriction, tagRestriction);
        var restrictions = influxQueryFields.getMeasurementNameOptional()
                .map(name -> Restrictions.and(RestrictionsFactory.measurement(name), necessaryRestrictions))
                .orElse(necessaryRestrictions);

        var query = FluxUtils.epochQuery(bucketName, restrictions)
                .pivot(
                        Set.of(CommonTags.COMPUTATION_ID, Columns.TIME),
                        Set.of(Columns.FIELD),
                        Columns.VALUE
                )
                .drop(InfluxDefaults.EXCLUDED_COLUMNS);

        return influxClient
                .getQueryApi()
                .query(query.toString())
                .stream()
                .map(FluxUtils::tableToComputationSeries)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

}
