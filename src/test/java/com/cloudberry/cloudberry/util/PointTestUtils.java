package com.cloudberry.cloudberry.util;

import com.influxdb.client.write.Point;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

// 16.08.20:  influx point can be only accessed with reflection api
public final class PointTestUtils {
    private PointTestUtils() {
    }

    public static Map<String, String> getTags(Point point) {
        return (Map<String, String>) ReflectionTestUtils.getField(point, "tags");
    }

    public static Map<String, Object> getFields(Point point) {
        return (Map<String, Object>) ReflectionTestUtils.getField(point, "fields");
    }

    public static Number getTime(Point point) {
        return (Number) ReflectionTestUtils.getField(point, "time");
    }

}
