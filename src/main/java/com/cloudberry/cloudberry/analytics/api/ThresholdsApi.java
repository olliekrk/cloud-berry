package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.Thresholds;

import java.util.List;

public interface ThresholdsApi {

    List<DataSeries> thresholdsExceedingSeries(String fieldName,
                                               Thresholds thresholds,
                                               CriteriaMode mode,
                                               InfluxQueryFields influxQueryFields);

}
