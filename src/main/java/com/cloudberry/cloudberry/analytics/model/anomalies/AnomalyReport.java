package com.cloudberry.cloudberry.analytics.model.anomalies;

import lombok.Value;
import org.bson.types.ObjectId;

@Value
public class AnomalyReport {
    ObjectId computationId;
    double stddev;
    double mean;
    double spread;
    double min;
    double max;
    double maxDiff;
}
