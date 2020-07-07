package com.cloudberry.cloudberry.db.common.service;

import java.util.List;

public interface LogsParser<T> {
    List<T> parseMeasurements(String rawLogs);
}
