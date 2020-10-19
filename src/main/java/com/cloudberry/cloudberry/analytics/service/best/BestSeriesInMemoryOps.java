package com.cloudberry.cloudberry.analytics.service.best;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationGoal;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BestSeriesInMemoryOps {

    public static List<DataSeries> nBestSeriesFrom(
            int n,
            String fieldName,
            Optimization optimization,
            Collection<DataSeries> dataSeries
    ) {
        var seriesSortedAscending = dataSeries
                .stream()
                .map(series -> {
                    var metricsValue = switch (optimization.getOptimizationKind()) {
                        case AREA_UNDER_CURVE -> getSeriesIntegral(fieldName, series);
                        case FINAL_VALUE -> getSeriesLastValue(fieldName, series);
                    };
                    return Tuple.of(series, metricsValue);
                })
                .sorted(Comparator.comparingDouble(Tuple2::_2))
                .map(Tuple2::_1);

        if (optimization.getOptimizationGoal().equals(OptimizationGoal.MAX)) {
            var list = seriesSortedAscending.collect(Collectors.toList());
            var size = list.size();
            return list.stream().skip(Math.max(0, size - n)).collect(Collectors.toList());
        } else {
            return seriesSortedAscending.limit(n).collect(Collectors.toList());
        }
    }

    /**
     * Compute the area under the connected points from the series by addition of trapezoids fields
     */
    private static double getSeriesIntegral(String fieldName, DataSeries series) {
        var timeToValue = series.getFieldValueByTime(fieldName, .0);
        if (timeToValue.isEmpty()) {
            return 0;
        }
        var timeToValueTail = io.vavr.collection.List.ofAll(timeToValue).tail();
        return Streams.zip(timeToValue.stream(), timeToValueTail.toJavaStream(), (pointA, pointB) -> {
            var a = pointA._2;
            var b = pointB._2;
            var height = pointA._1.toEpochMilli() - pointB._1.toEpochMilli();
            return (a + b) * height / 2.;
        }).mapToDouble(Double::doubleValue).sum();
    }

    private static double getSeriesLastValue(String fieldName, DataSeries series) {
        var lastPoint = Iterables.getLast(series.getDataSortedByTime(), Map.<String, Object>of());
        return Optional.ofNullable(lastPoint)
                .flatMap(point -> Optional.ofNullable((Double) lastPoint.get(fieldName)))
                .orElse(0.);
    }

}
