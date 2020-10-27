package com.cloudberry.cloudberry.analytics.service.thresholds;

import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.thresholds.ThresholdsInfo;
import com.google.common.collect.Streams;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public abstract class ThresholdsInMemoryOps {

    /**
     * Checks if any series within dataSeries have values that exceed thresholds relatively to relativeSeries.
     * <p>
     * If the criteria mode is ANY, then the closest values (by timestamps) will be compared.
     * (Assumes that no extra timestamps shift is required)
     */
    public static List<DataSeries> thresholdsExceedingSeriesRelatively(
            String fieldName,
            ThresholdsInfo thresholdsInfo,
            CriteriaMode mode,
            Collection<DataSeries> dataSeries,
            DataSeries relativeSeries
    ) {
        var relativeValues = relativeSeries.getFieldValueByTime(fieldName);
        Function<List<Tuple2<Instant, Double>>, Boolean> checker = switch (mode) {
            case AVERAGE -> v -> isSeriesAverageExceedingThresholds(v, relativeValues, thresholdsInfo);
            case FINAL -> v -> isSeriesFinalValueExceedingThresholds(v, relativeValues, thresholdsInfo);
            case ANY -> v -> isSeriesAnyValueExceedingThresholds(v, relativeValues, thresholdsInfo);
        };
        return dataSeries.stream()
                .filter(s -> {
                    var values = s.getFieldValueByTime(fieldName);
                    return checker.apply(values);
                })
                .collect(Collectors.toList());
    }

    public static boolean isSeriesAnyValueExceedingThresholds(
            List<Tuple2<Instant, Double>> values,
            List<Tuple2<Instant, Double>> relativeValues,
            ThresholdsInfo thresholdsInfo
    ) {
        return values.stream().anyMatch(point -> {
            var timestamp = point._1;
            var value = point._2;

            // get points from relativeSeries, one before and one after
            var relativeBeforeOpt =
                    Streams.findLast(
                            relativeValues.stream().takeWhile(t -> t._1.isBefore(timestamp) || t._1.equals(timestamp)));
            var relativeAfterOpt =
                    relativeValues.stream().dropWhile(t -> t._1.isBefore(timestamp)).limit(1).findFirst();

            if (relativeAfterOpt.isPresent() && relativeBeforeOpt.isPresent()) {
                // compute relative point as if it belongs to linear function between point before and point after
                var relativeBefore = relativeBeforeOpt.get();
                var relativeAfter = relativeAfterOpt.get();
                var linearCoefficient = (relativeAfter._2 - relativeBefore._2) /
                        (relativeAfter._1.toEpochMilli() - relativeBefore._1.toEpochMilli());
                var valueToCompare = relativeBefore._2 +
                        linearCoefficient * (timestamp.toEpochMilli() - relativeBefore._1.toEpochMilli());
                return isExceedingThresholds(value, valueToCompare, thresholdsInfo);
            } else {
                // case when there is only single relative value to compare to
                return relativeAfterOpt
                        .map(relativePoint -> isExceedingThresholds(value, relativePoint._2, thresholdsInfo))
                        .orElseGet(() -> relativeBeforeOpt
                                .stream()
                                .anyMatch(relativePoint -> isExceedingThresholds(value, relativePoint._2,
                                                                                 thresholdsInfo
                                )));
            }
        });
    }

    public static boolean isSeriesFinalValueExceedingThresholds(
            List<Tuple2<Instant, Double>> values,
            List<Tuple2<Instant, Double>> relativeValues,
            ThresholdsInfo thresholdsInfo
    ) {
        var lastValue = Streams.findLast(values.stream());
        var lastRelativeValue = Streams.findLast(relativeValues.stream());

        return lastValue.isPresent()
                && lastRelativeValue.isPresent()
                && isExceedingThresholds(lastValue.map(Tuple2::_2).get(), lastRelativeValue.map(Tuple2::_2).get(),
                                         thresholdsInfo
        );
    }

    public static boolean isSeriesAverageExceedingThresholds(
            List<Tuple2<Instant, Double>> values,
            List<Tuple2<Instant, Double>> relativeValues,
            ThresholdsInfo thresholdsInfo
    ) {
        Function<List<Tuple2<Instant, Double>>, Double> averageValue =
                v -> v.stream().map(Tuple2::_2).collect(Collectors.averagingDouble(Double::doubleValue));

        return isExceedingThresholds(averageValue.apply(values), averageValue.apply(relativeValues), thresholdsInfo);
    }


    private static boolean isExceedingThresholds(double value, double relativeValue, ThresholdsInfo thresholdsInfo) {
        var lowerOpt = Optional.ofNullable(thresholdsInfo.getThresholds().getLower());
        var upperOpt = Optional.ofNullable(thresholdsInfo.getThresholds().getUpper());

        var absoluteOpts = switch (thresholdsInfo.getType()) {
            case ABSOLUTE -> Tuple.of(lowerOpt, upperOpt);
            case PERCENTS -> Tuple
                    .of(lowerOpt.map(t -> relativeValue * (1. - t)), upperOpt.map(t -> relativeValue * (1. + t)));
        };

        return absoluteOpts._1.stream().anyMatch(lower -> value < lower)
                || absoluteOpts._2.stream().anyMatch(upper -> value > upper);
    }


}
