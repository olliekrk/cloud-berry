package com.cloudberry.cloudberry.parsing.model.age;

import com.cloudberry.cloudberry.parsing.model.ParsedLogs;
import com.influxdb.client.write.Point;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
public class AgeParsedLogs extends ParsedLogs {
    String configurationName;
    Map<String, Object> configurationParameters;

    public AgeParsedLogs(List<Point> points,
                         @Nullable String configurationName,
                         Map<String, Object> configurationParameters) {
        super(points);
        this.configurationName = configurationName;
        this.configurationParameters = configurationParameters;
    }
}
