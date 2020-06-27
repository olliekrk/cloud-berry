package com.cloudberry.cloudberry.db.influx;

import com.influxdb.client.domain.WritePrecision;

public interface InfluxDefaults {
    WritePrecision WRITE_PRECISION = WritePrecision.NS;
}
