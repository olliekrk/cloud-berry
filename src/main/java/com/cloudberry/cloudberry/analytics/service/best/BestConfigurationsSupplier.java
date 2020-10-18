package com.cloudberry.cloudberry.analytics.service.best;

import com.cloudberry.cloudberry.analytics.api.BestConfigurationsApi;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.service.configurations.ConfigurationSeriesCreator;
import com.google.common.collect.Comparators;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BestConfigurationsSupplier implements BestConfigurationsApi {
    private final ConfigurationSeriesCreator configurationSeriesCreator;

    public List<DataSeries> nBestConfigurations(
            int n,
            String fieldName,
            Optimization optimization,
            InfluxQueryFields influxQueryFields,
            List<ObjectId> configurationsIds
    ) {
        var configurationsSeries = configurationsIds.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        id -> configurationSeriesCreator.createMovingAverageConfigurationSeries(
                                fieldName,
                                influxQueryFields,
                                id
                        )
                ));

        var configurationsMetrics = configurationsSeries.entrySet().stream()
                .map(entry -> {
                    var metricsValue = switch (optimization.getOptimizationKind()) {
                        case AREA_UNDER_CURVE -> getSeriesIntegral(fieldName, entry.getValue());
                        case FINAL_VALUE -> getSeriesLastValue(fieldName, entry.getValue());
                    };
                    return Map.entry(entry.getKey(), metricsValue);
                })
                .sorted(Map.Entry.comparingByValue(Double::compareTo));

        return (switch (optimization.getOptimizationGoal()) {
            case MAX -> configurationsMetrics.collect(Comparators.greatest(n, Map.Entry.comparingByValue(Double::compareTo)));
            case MIN -> configurationsMetrics.collect(Comparators.least(n, Map.Entry.comparingByValue(Double::compareTo)));
        }).stream()
                .map(Map.Entry::getKey)
                .map(configurationsSeries::get)
                .collect(Collectors.toList());
    }

    /**
     * Compute the area under the connected points from the series by addition of trapezoids fields
     */
    private double getSeriesIntegral(String fieldName, DataSeries series) {
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

    private double getSeriesLastValue(String fieldName, DataSeries series) {
        var lastPoint = Iterables.getLast(series.getDataSortedByTime(), Map.<String, Object>of());
        return Optional.ofNullable(lastPoint)
                .flatMap(point -> Optional.ofNullable((Double) lastPoint.get(fieldName)))
                .orElse(0.);
    }

}
