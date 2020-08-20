package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.OptionalQueryFields;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;

import java.util.List;

public interface BestSeriesApi {

    List<DataSeries> nBestSeries(int n,
                                 String fieldName,
                                 Optimization optimization,
                                 OptionalQueryFields optionalQueryFields);

}
