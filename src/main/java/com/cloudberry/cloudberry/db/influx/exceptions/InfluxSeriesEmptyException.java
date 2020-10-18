package com.cloudberry.cloudberry.db.influx.exceptions;

public class InfluxSeriesEmptyException extends InfluxException {
    public InfluxSeriesEmptyException(String message) {
        super(message);
    }
}
