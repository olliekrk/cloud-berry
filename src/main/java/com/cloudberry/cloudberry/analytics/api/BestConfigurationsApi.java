package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.InfluxQueryFields;
import com.cloudberry.cloudberry.analytics.model.optimization.Optimization;
import org.bson.types.ObjectId;

import java.util.List;

public interface BestConfigurationsApi {

    // todo: this could be more generic & - take List<DataSeries> as input
    List<DataSeries> nBestConfigurations(
            int n,
            String fieldName,
            Optimization optimization,
            InfluxQueryFields influxQueryFields,
            List<ObjectId> configurationsIds
    );

}
