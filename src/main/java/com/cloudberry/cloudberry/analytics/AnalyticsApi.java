package com.cloudberry.cloudberry.analytics;

import com.cloudberry.cloudberry.analytics.api.BestSeriesApi;
import com.cloudberry.cloudberry.analytics.api.MeanApi;
import com.cloudberry.cloudberry.analytics.api.MovingAverageApi;
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
    MeanApi meanApi;
    MovingAverageApi movingAverageApi;
    BestSeriesApi bestSeriesApi;
}
