package com.cloudberry.cloudberry.service;

import java.util.List;

public interface LogsParser<T> {
    List<T> parseMeasurements(String rawLogs);
}
