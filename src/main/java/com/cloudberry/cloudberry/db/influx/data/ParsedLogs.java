package com.cloudberry.cloudberry.db.influx.data;

import com.influxdb.client.write.Point;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class ParsedLogs {
    private final String bucketName;
    private final List<Point> points;
}
