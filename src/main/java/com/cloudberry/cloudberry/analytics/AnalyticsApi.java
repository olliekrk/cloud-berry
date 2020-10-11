package com.cloudberry.cloudberry.analytics;

import com.cloudberry.cloudberry.analytics.api.*;
import com.cloudberry.cloudberry.analytics.service.average.moving.MovingAverageAvg;
import com.cloudberry.cloudberry.analytics.service.average.moving.MovingAverageStd;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.stereotype.Component;

/**
 * Facade class for whole analytics API
 */
@Component
@Value
@RequiredArgsConstructor
public class AnalyticsApi {
    SeriesApi seriesApi;
    BestSeriesApi bestSeriesApi;
    BestConfigurationsApi bestConfigurationsApi;
    MovingAverageStd movingAverageStd;
    MovingAverageAvg movingAverageAvg;
    ThresholdsApi thresholdsApi;
    AnomaliesApi anomaliesApi;
}
