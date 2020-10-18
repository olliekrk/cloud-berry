package com.cloudberry.cloudberry.db.influx.exceptions;

public abstract class InfluxException extends RuntimeException {
    public InfluxException(String message) {
        super(message);
    }
}
