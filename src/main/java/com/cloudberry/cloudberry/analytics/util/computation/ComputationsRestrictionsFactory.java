package com.cloudberry.cloudberry.analytics.util.computation;

import com.cloudberry.cloudberry.common.syntax.ListSyntax;
import com.cloudberry.cloudberry.db.influx.InfluxDefaults;
import com.cloudberry.cloudberry.db.influx.util.RestrictionsFactory;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import org.bson.types.ObjectId;

import java.util.List;

public final class ComputationsRestrictionsFactory {

    public static Restrictions computationIdIn(List<ObjectId> computationsIds) {
        return RestrictionsFactory.tagIn(
                InfluxDefaults.CommonTags.COMPUTATION_ID,
                ListSyntax.mapped(computationsIds, ObjectId::toHexString)
        );
    }

}
