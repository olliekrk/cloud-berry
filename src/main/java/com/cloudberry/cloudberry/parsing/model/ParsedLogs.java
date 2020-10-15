package com.cloudberry.cloudberry.parsing.model;

import com.influxdb.client.write.Point;

import java.util.List;

public class ParsedLogs {
    private final List<Point> points;

    public ParsedLogs(List<Point> points) {
        this.points = points;
    }

    public List<Point> getPoints() {
        return points;
    }
}
