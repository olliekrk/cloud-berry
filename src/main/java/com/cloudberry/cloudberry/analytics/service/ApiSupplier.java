package com.cloudberry.cloudberry.analytics.service;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.config.influx.InfluxConfig;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Optional;

abstract class ApiSupplier {

    protected static String bucketNameOrDefault(@Nullable String bucketName, InfluxConfig influxConfig) {
        return Optional.ofNullable(bucketName).orElseGet(influxConfig::getDefaultBucketName);
    }

    protected static Flux epochQuery(String bucketName,
                                     Restrictions restrictions) {
        return Flux
                .from(bucketName)
                .range(Instant.EPOCH)
                .filter(restrictions);
    }

    protected static Flux epochQueryByComputationId(String bucketName,
                                                    Restrictions restrictions) {
        return epochQuery(bucketName, restrictions)
                .groupBy(CommonTags.COMPUTATION_ID);
    }

    protected static Optional<DataSeries> tableToComputationSeries(FluxTable fluxTable) {
        var records = fluxTable.getRecords();
        if (records.isEmpty()) {
            return Optional.empty();
        } else {
            var recordsHead = records.get(0);
            var computationId = (String) recordsHead.getValueByKey(CommonTags.COMPUTATION_ID);
            return Optional.of(new DataSeries(computationId, ListSyntax.mapped(records, FluxRecord::getValues)));
        }
    }

}
