package com.cloudberry.cloudberry.db.influx.service;

import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import com.cloudberry.cloudberry.common.syntax.CollectionSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.util.OffsetsFactory;
import com.influxdb.client.InfluxDBClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class InfluxDataRemover {
    private final InfluxDBClient influxClient;
    private final InfluxOrganizationService influxOrganizationService;

    public void deleteData(
            InfluxQueryFields influxQueryFields,
            Map<String, String> tags
    ) {
        var start = OffsetsFactory.epoch();
        var stop = OffsetsFactory.now();
        var bucket = influxQueryFields.getBucketName();
        influxClient.getDeleteApi().delete(
                start,
                stop,
                buildDeletePredicate(influxQueryFields.getMeasurementNameOptional(), tags),
                bucket,
                influxOrganizationService.getDefaultOrganizationId()
        );
    }

    public void deleteDataWithDifferentPossibleTagValues(
            InfluxQueryFields influxQueryFields,
            String tagName,
            List<String> tagValues
    ) {
        var start = OffsetsFactory.epoch();
        var stop = OffsetsFactory.now();
        var bucket = influxQueryFields.getBucketName();

        tagValues.forEach(tagValue -> {
            val predicates = new HashSet<String>();
            predicates.add(format("%s=\"%s\"", tagName, tagValue));

            // add measurement name predicate if provided
            influxQueryFields.getMeasurementNameOptional()
                    .map(createSingleTagQuery())
                    .ifPresent(predicates::add);

            influxClient.getDeleteApi().delete(
                    start,
                    stop,
                    createAndPredicate(predicates),
                    bucket,
                    influxOrganizationService.getDefaultOrganizationId()
            );

        });
    }

    private static String buildDeletePredicate(
            Optional<String> measurementName,
            Map<String, String> tags
    ) {
        // build predicates set using tags
        var predicates = CollectionSyntax.mapped(
                tags.entrySet(),
                entry -> format("%s=\"%s\"", entry.getKey(), entry.getValue()),
                HashSet::new
        );

        // add measurement name predicate if provided
        measurementName
                .map(createSingleTagQuery())
                .ifPresent(predicates::add);

        return createAndPredicate(predicates);
    }

    @NotNull
    private static Function<String, String> createSingleTagQuery() {
        return name -> format("%s=\"%s\"", InfluxDefaults.Columns.MEASUREMENT, name);
    }

    @NotNull
    private static String createAndPredicate(Set<String> predicates) {
        return String.join(" and ", predicates);
    }
}
