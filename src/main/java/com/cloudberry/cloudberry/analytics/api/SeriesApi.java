package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.filters.DataFilters;
import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import org.bson.types.ObjectId;

import java.util.List;

public interface SeriesApi {

    List<DataSeries> computationsSeries(
            String fieldName,
            List<ObjectId> computationsIds,
            InfluxQueryFields influxQueryFields
    );

    List<DataSeries> computationsSeries(
            String fieldName,
            List<ObjectId> computationsIds,
            InfluxQueryFields influxQueryFields,
            DataFilters dataFilters
    );

}
