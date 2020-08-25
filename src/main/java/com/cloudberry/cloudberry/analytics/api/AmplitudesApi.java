package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Used for finding parameters' anomalies.
 */
public interface AmplitudesApi {

    List<DataSeries> nLowestAmplitudeSeries(int n,
                                            String fieldName,
                                            CriteriaMode mode,
                                            @Nullable String measurementNameOpt,
                                            @Nullable String bucketNameOpt);

    List<DataSeries> nHighestAmplitudeSeries(int n,
                                             String fieldName,
                                             CriteriaMode mode,
                                             @Nullable String measurementNameOpt,
                                             @Nullable String bucketNameOpt);

}
