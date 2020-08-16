package com.cloudberry.cloudberry.parsing.model;

import com.influxdb.client.write.Point;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Value;

import java.util.List;
import java.util.Map;

public class ParsedLogs {
    private final List<Point> points;

    public ParsedLogs(List<Point> points) {
        this.points = points;
    }

    public List<Point> getPoints() {
        return points;
    }
}
