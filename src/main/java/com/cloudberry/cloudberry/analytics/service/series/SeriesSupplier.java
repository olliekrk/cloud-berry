package com.cloudberry.cloudberry.analytics.service.series;

import com.cloudberry.cloudberry.analytics.api.SeriesApi;
import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.filters.DataFilters;
import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.util.FluxUtils;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.Columns;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.client.InfluxDBClient;
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
            String fieldName, List<ObjectId> computationsIds, InfluxQueryFields influxQueryFields
    ) {
        return computationsSeries(fieldName, computationsIds, influxQueryFields, DataFilters.empty());
    }

    @Override
    public List<DataSeries> computationsSeries(
            String fieldName,
            List<ObjectId> computationsIds,
            InfluxQueryFields influxQueryFields,
            DataFilters dataFilters
    ) {
        if (computationsIds.isEmpty()) {
            return Collections.emptyList();
        }
        var bucketName = influxQueryFields.getBucketName();
        var restrictions = RestrictionsFactory.combine(fieldName, dataFilters, influxQueryFields, computationsIds);

        var query = FluxUtils.epochQuery(bucketName, restrictions)
                .pivot(
                        Set.of(CommonTags.COMPUTATION_ID, Columns.TIME),
                        Set.of(Columns.FIELD),
                        Columns.VALUE
                )
                .drop(InfluxDefaults.EXCLUDED_COLUMNS)
                .groupBy(List.of(CommonTags.COMPUTATION_ID))
                .sort(List.of(Columns.TIME));

        return influxClient
                .getQueryApi()
                .query(query.toString())
                .stream()
                .map(FluxUtils::tableToComputationSeries)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

}
