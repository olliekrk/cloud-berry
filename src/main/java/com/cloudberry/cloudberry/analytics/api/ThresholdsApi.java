package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.CriteriaMode;
import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.filters.DataFilters;
import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.thresholds.Thresholds;
import com.cloudberry.cloudberry.analytics.model.thresholds.ThresholdsInfo;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ThresholdsApi {

    List<DataSeries> thresholdsExceedingSeries(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields,
            DataFilters dataFilters
    );

    List<DataSeries> thresholdsExceedingSeriesFrom(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            InfluxQueryFields influxQueryFields,
            List<ObjectId> computationIds,
            DataFilters dataFilters
    );

    List<DataSeries> thresholdsExceedingSeriesFrom(
            String fieldName,
            Thresholds thresholds,
            CriteriaMode mode,
            Map<ObjectId, DataSeries> dataSeries
    );

    /**
     * From all of `dataSeries`, return those series which are strongly deviating from given `relativeSeries`,
     * according to given criteria `mode` and values specified in `thresholdsInfo`.
     */
    List<DataSeries> thresholdsExceedingSeriesRelatively(
            String fieldName,
            ThresholdsInfo thresholdsInfo,
            CriteriaMode mode,
            Collection<DataSeries> dataSeries,
            DataSeries relativeSeries
    );

}
