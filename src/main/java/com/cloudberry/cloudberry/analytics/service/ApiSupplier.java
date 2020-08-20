package com.cloudberry.cloudberry.analytics.service;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ApiSupplier {

    public static String bucketNameOrDefault(Optional<String> bucketName, InfluxConfig influxConfig) {
        return bucketName.orElseGet(influxConfig::getDefaultBucketName);
    }

    static Flux epochQuery(String bucketName,
                           Restrictions restrictions) {
        return Flux
                .from(bucketName)
                .range(Instant.EPOCH)
                .filter(restrictions);
    }

    public static Flux epochQueryByComputationId(String bucketName,
                                                 Restrictions restrictions) {
        return epochQuery(bucketName, restrictions)
                .groupBy(CommonTags.COMPUTATION_ID);
    }

    public static Optional<DataSeries> tableToComputationSeries(FluxTable fluxTable) {
        var records = fluxTable.getRecords();
        if (records.isEmpty()) {
            return Optional.empty();
        } else {
            var recordsHead = records.get(0);
            var data = records
                    .stream()
                    .peek(ApiSupplier::filterOutInfluxColumns)
                    .map(FluxRecord::getValues)
                    .collect(Collectors.toList());
            var computationId = (String) recordsHead.getValueByKey(CommonTags.COMPUTATION_ID);
            return Optional.of(new DataSeries(computationId, data));
        }
    }

    static Stream<Instant> tableToTime(FluxTable fluxTable) {
        return fluxTable.getRecords().stream().map(FluxRecord::getTime);
    }

    static void filterOutInfluxColumns(FluxRecord record) {
        record.getValues().keySet().removeAll(InfluxDefaults.EXCLUDED_COLUMNS);
    }

    public static Restrictions getRestrictions(String fieldName, Optional<String> measurementNameOpt) {
        var fieldRestriction = RestrictionsFactory.hasField(fieldName);
        return measurementNameOpt.map(name -> Restrictions.and(RestrictionsFactory.measurement(name)))
                .orElse(fieldRestriction);
    }
}
