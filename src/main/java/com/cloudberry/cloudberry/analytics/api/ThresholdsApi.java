package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.Thresholds;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

public interface ThresholdsApi {

    List<DataSeries> thresholdsExceedingSeries(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields);

    List<DataSeries> thresholdsExceedingSeriesFrom(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields,
            List<ObjectId> computationIds
    );

    List<DataSeries> thresholdsExceedingSeriesFrom(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            Map<ObjectId, DataSeries> dataSeries
    );
}
