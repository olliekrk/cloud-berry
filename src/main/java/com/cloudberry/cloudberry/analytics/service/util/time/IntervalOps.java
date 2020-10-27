package com.cloudberry.cloudberry.analytics.service.util.time;

import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.time.TimeRange;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class IntervalOps {
    private final static Duration DEFAULT_DURATION = Duration.ofNanos(1_000_000_000L); // 1 second

    public static Duration suitableMovingBucketDuration(List<DataSeries> series) {
        var numberOfSeries = series.size();
        if (numberOfSeries == 0) {
            log.warn("Default bucket interval used as series are empty!");
            return DEFAULT_DURATION;
        }

        var seriesRanges = series.stream()
                .flatMap(s -> s.getTimeRange().stream())
                .collect(Collectors.toList());

        var minTimeOpt = seriesRanges.stream().map(TimeRange::getStart).min(Instant::compareTo);
        var maxTimeOpt = seriesRanges.stream().map(TimeRange::getEnd).max(Instant::compareTo);
        if (minTimeOpt.isEmpty()) {
            log.warn("Invalid time markers on series!");
            return DEFAULT_DURATION;
        }

        var seriesDuration = Duration.between(minTimeOpt.get(), maxTimeOpt.get());
        var seriesSizeAverage = series.stream()
                .map(DataSeries::getDataSize)
                .collect(Collectors.averagingInt(Integer::intValue))
                .longValue();

        return seriesDuration.dividedBy(Math.max(1, seriesSizeAverage));
    }

}
