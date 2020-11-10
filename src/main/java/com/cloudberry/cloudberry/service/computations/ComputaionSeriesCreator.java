package com.cloudberry.cloudberry.service.computations;

import com.cloudberry.cloudberry.analytics.model.basic.SeriesInfo;
import org.bson.types.ObjectId;

public final class ComputaionSeriesCreator {
    public static String computationSeriesName(ObjectId computationId) {
        return computationSeriesName(computationId.toHexString());
    }

    public static String computationSeriesName(String computationId) {
        return String.format("computation_%s", computationId);
    }

    public static SeriesInfo computationSeriesInfo(ObjectId computationId) {
        return computationSeriesInfo(computationId.toHexString());
    }

    public static SeriesInfo computationSeriesInfo(String computationId) {
        return new SeriesInfo(computationSeriesName(computationId), computationId);
    }
}
