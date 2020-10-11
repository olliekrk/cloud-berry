package com.cloudberry.cloudberry.analytics.service.best;

import com.cloudberry.cloudberry.analytics.api.BestConfigurationsApi;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.service.configurations.ConfigurationSeriesCreator;
import com.google.common.collect.Comparators;
import com.google.common.collect.Iterables;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BestConfigurationsSupplier implements BestConfigurationsApi {
    private static final int MAX_INTEGRAL_EVALUATIONS = 10_000;
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

    private double getSeriesIntegral(String fieldName, DataSeries series) {
        var timeToValue = series.getFieldValueByTime(fieldName, .0);
        var timeToValueMap = timeToValue.stream().collect(Collectors.toMap(Tuple2::_1, Tuple2::_2));
        UnivariateFunction univariateFunction = milliTime -> {
            var timestamp = Instant.ofEpochMilli((long) milliTime);
            if (timeToValueMap.containsKey(timestamp)) {
                return (Double) timeToValueMap.get(timestamp);
            } else {
                var aOpt = timeToValue.stream()
                        .takeWhile(t -> t._1.isBefore(timestamp) || t._1.equals(timestamp))
                        .reduce((__, next) -> next);
                var bOpt = timeToValue.stream()
                        .filter(t -> t._1.equals(timestamp) || t._1.isAfter(timestamp))
                        .findFirst();

                if (aOpt.isEmpty() || bOpt.isEmpty()) {
                    log.warn("Something went wrong when integrating function for series: {}", series.getSeriesName());
                    return .0;
                } else {
                    // linear function
                    var a = aOpt.get();
                    var b = bOpt.get();
                    var coefficient = (b._2 - a._2) / (b._1.toEpochMilli() - a._1.toEpochMilli());
                    return coefficient * milliTime + b._2;
                }
            }
        };

        return series.getTimeRange().map(range -> new TrapezoidIntegrator().integrate(
                MAX_INTEGRAL_EVALUATIONS,
                univariateFunction,
                range.getStart().toEpochMilli(),
                range.getEnd().toEpochMilli()
        )).orElse(.0);
    }

    private double getSeriesLastValue(String fieldName, DataSeries series) {
        Map<String, Object> lastPoint = Iterables.getLast(series.getDataSortedByTime(), Map.of());
        return (double) Optional.ofNullable(lastPoint)
                .flatMap(point -> Optional.ofNullable(lastPoint.get(fieldName)))
                .orElse(.0);
    }

}
