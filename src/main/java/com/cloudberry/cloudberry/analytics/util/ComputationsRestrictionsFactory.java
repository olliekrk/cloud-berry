package com.cloudberry.cloudberry.analytics.util;

import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

import java.util.List;

public final class ComputationsRestrictionsFactory {

    public static Restrictions getComputationsRestrictions(List<ObjectId> computationsIds,
                                                           String fieldName,
                                                           @NonNull String measurementName) {
        return Restrictions.and(
                getFieldRestrictions(fieldName),
                getMeasurementNameRestriction(measurementName),
                getComputationIdRestriction(computationsIds));
    }

    public static Restrictions getComputationsRestrictions(List<ObjectId> computationsIds, String fieldName) {
        return Restrictions.and(
                getFieldRestrictions(fieldName),
                getComputationIdRestriction(computationsIds));
    }

    public static Restrictions getFieldAndMeasurementNameRestrictions(String fieldName,
                                                                      @NonNull String measurementName) {
        return Restrictions.and(
                getFieldRestrictions(fieldName),
                getMeasurementNameRestriction(measurementName));
    }

    public static Restrictions getComputationIdRestriction(List<ObjectId> computationsIds) {
        return RestrictionsFactory.tagIn(
                InfluxDefaults.CommonTags.COMPUTATION_ID,
                ListSyntax.mapped(computationsIds, ObjectId::toHexString));
    }

    public static Restrictions getFieldRestrictions(String fieldName) {
        return RestrictionsFactory.hasField(fieldName);
    }

    public static Restrictions getMeasurementNameRestriction(String measurementName) {
        return RestrictionsFactory.measurement(measurementName);
    }

}
