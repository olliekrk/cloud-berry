package com.cloudberry.cloudberry.db.influx;

import com.influxdb.client.domain.WritePrecision;

public interface InfluxDefaults {
    WritePrecision WRITE_PRECISION = WritePrecision.NS;

    interface Columns {
        String TIME = "_time";
        String VALUE = "_value";
        String FIELD = "_field";
        String MEASUREMENT = "_measurement";
    }

    interface CommonTags {
        String COMPUTATION_ID = "computationId";
    }
}
