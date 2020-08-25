package com.cloudberry.cloudberry.analytics.service;

import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

import java.util.List;

public final class RestrictionsGenerator {

    public static Restrictions getRestrictionsComputationIds(List<ObjectId> computationsIds,
                                                             String fieldName,
                                                             @NonNull String measurementName) {
        return getRestrictionsComputationIdsWithOther(
                computationsIds,
                getFieldAndMeasurementNameRestrictions(fieldName, measurementName));
    }

    public static Restrictions getRestrictionsComputationIds(List<ObjectId> computationsIds, String fieldName) {
        return getRestrictionsComputationIdsWithOther(
                computationsIds,
                getFieldAndMeasurementNameRestrictions(fieldName));
    }

    private static Restrictions getRestrictionsComputationIdsWithOther(List<ObjectId> computationsIds,
                                                                       Restrictions restrictions) {
        var computationIdRestriction = getComputationIdRestriction(computationsIds);
        return Restrictions.and(computationIdRestriction, restrictions);
    }

    private static Restrictions getComputationIdRestriction(List<ObjectId> computationsIds) {
        return RestrictionsFactory
                .tagIn(
                        InfluxDefaults.CommonTags.COMPUTATION_ID,
                        ListSyntax.mapped(computationsIds, ObjectId::toHexString));
    }

    public static Restrictions getFieldAndMeasurementNameRestrictions(String fieldName) {
        return RestrictionsFactory.hasField(fieldName);
    }

    public static Restrictions getFieldAndMeasurementNameRestrictions(String fieldName,
                                                                      @NonNull String measurementName) {
        return Restrictions.and(
                getFieldAndMeasurementNameRestrictions(fieldName),
                RestrictionsFactory.measurement(measurementName)
        );
    }
}
