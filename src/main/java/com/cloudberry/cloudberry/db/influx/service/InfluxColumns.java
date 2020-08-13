package com.cloudberry.cloudberry.db.influx.service;

import java.util.List;

public final class InfluxColumns {
    public static final String START = "_start";
    public static final String STOP = "_stop";
    public static final String RESULT = "result";
    public static final String TABLE = "table";

    public static final List<String> EXCLUDED_COLUMNS = List.of(START, STOP);
}
