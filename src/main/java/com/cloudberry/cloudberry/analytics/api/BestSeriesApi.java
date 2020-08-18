package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.OptimizationKind;
import org.springframework.lang.Nullable;

import java.util.List;

public interface BestSeriesApi {

    List<DataSeries> nBestSeriesForField(int n,
                                         String fieldName,
                                         OptimizationGoal optimizationGoal,
                                         OptimizationKind optimizationKind,
                                         @Nullable String measurementName,
                                         @Nullable String bucketName);

}
