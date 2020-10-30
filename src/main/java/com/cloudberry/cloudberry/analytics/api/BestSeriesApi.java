package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.basic.DataSeries;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import com.cloudberry.cloudberry.analytics.model.query.InfluxQueryFields;
import org.bson.types.ObjectId;

import java.util.Collection;
import java.util.List;

public interface BestSeriesApi {

    List<DataSeries> nBestSeries(
            int n,
            String fieldName,
            Optimization optimization,
            InfluxQueryFields influxQueryFields
    );

    List<DataSeries> nBestSeriesFrom(
            int n,
            String fieldName,
            Optimization optimization,
            InfluxQueryFields influxQueryFields,
            List<ObjectId> computationIds
    );

    List<DataSeries> nBestSeriesFrom(
            int n,
            String fieldName,
            Optimization optimization,
            Collection<DataSeries> seriesMap
    );

}
