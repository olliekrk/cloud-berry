package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.model.OptimizationKind;
import org.springframework.lang.Nullable;

import java.util.List;

public interface BestSeriesApi {

    List<DataSeries> nBestSeries(int n,
                                 String fieldName,
                                 OptimizationGoal optimizationGoal,
                                 OptimizationKind optimizationKind,
                                 @Nullable String measurementNameOpt,
                                 @Nullable String bucketNameOpt);

}
