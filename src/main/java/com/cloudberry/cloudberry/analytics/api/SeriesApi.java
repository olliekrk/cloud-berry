package com.cloudberry.cloudberry.analytics.api;

import com.cloudberry.cloudberry.analytics.model.DataSeries;
import org.bson.types.ObjectId;
import org.springframework.lang.Nullable;

import java.util.List;

public interface SeriesApi {

    List<DataSeries> computationsSeries(List<ObjectId> computationsIds,
                                        @Nullable String measurementNameOpt,
                                        @Nullable String bucketNameOpt);
}
