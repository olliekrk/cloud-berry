package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import org.bson.types.ObjectId;

import java.util.List;

public interface BestSeriesApi {

    List<DataSeries> nBestSeries(int n,
                                 String fieldName,
                                 Optimization optimization,
                                 InfluxQueryFields influxQueryFields);

    List<DataSeries> nBestSeriesFrom(int n,
                                     String fieldName,
                                     Optimization optimization,
                                     InfluxQueryFields influxQueryFields,
                                     List<ObjectId> computationIds);

}
