package com.cloudberry.cloudberry.db.influx;

import com.influxdb.client.domain.WritePrecision;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public interface InfluxDefaults {
    WritePrecision WRITE_PRECISION = WritePrecision.NS;

    interface Columns {
        String TIME = "_time";
        String VALUE = "_value";
        String FIELD = "_field";
        String MEASUREMENT = "_measurement";
        String START = "_start";
        String STOP = "_stop";
        String RESULT = "result";
        String TABLE = "table";
    }

    interface Comparators {
        Comparator<Map<String, Object>> byTime =
                Comparator.comparing(point -> (Instant) point.get(Columns.TIME), Instant::compareTo);
    }

    List<String> EXCLUDED_COLUMNS = List.of(
            Columns.START,
            Columns.STOP,
            Columns.RESULT,
            Columns.TABLE
    );

    interface CommonTags {
        String COMPUTATION_ID = "computationId";
    }
}
