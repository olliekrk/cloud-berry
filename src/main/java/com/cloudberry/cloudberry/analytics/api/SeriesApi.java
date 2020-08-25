package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import com.cloudberry.cloudberry.analytics.model.OptionalQueryFields;
import org.bson.types.ObjectId;

import java.util.List;

public interface SeriesApi {

    List<DataSeries> computationsSeries(String fieldName,
                                        List<ObjectId> computationsIds,
                                        OptionalQueryFields optionalQueryFields);

}
