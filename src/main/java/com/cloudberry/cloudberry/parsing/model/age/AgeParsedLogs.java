package com.cloudberry.cloudberry.parsing.model.age;

import com.cloudberry.cloudberry.parsing.model.ParsedLogs;
import com.influxdb.client.write.Point;
import lombok.Value;

import java.util.List;
import java.util.Map;

@Value
public class AgeParsedLogs extends ParsedLogs {
    String configurationName;
    Map<String, Object> configurationParameters;

    public AgeParsedLogs(List<Point> points,
                         String configurationName,
                         Map<String, Object> configurationParameters) {
        super(points);
        this.configurationName = configurationName;
        this.configurationParameters = configurationParameters;
    }
}
