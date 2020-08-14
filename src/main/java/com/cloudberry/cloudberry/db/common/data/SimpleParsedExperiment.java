package com.cloudberry.cloudberry.db.common.data;

import com.influxdb.client.write.Point;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Value
public class SimpleParsedExperiment {
    String configurationName;
    Map<String, Object> configurationParameters;
    List<Point> points;
}
