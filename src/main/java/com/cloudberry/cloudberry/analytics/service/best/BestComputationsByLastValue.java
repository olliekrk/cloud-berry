package com.cloudberry.cloudberry.analytics.service.best;

import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationGoal;
import com.cloudberry.cloudberry.analytics.util.FluxUtils;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;

import java.util.Set;

import static com.cloudberry.cloudberry.db.influx.InfluxDefaults.Columns.VALUE;
import static com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags.COMPUTATION_ID;

class BestComputationsByLastValue extends BestComputations {

    Flux getBest(int n,
                 OptimizationGoal optimizationGoal,
                 Restrictions restrictions,
                 String bucketName) {
        var preparedFlux = FluxUtils.epochQueryByComputationId(bucketName, restrictions)
                .last() // gets last value from each group
                .keep(Set.of(VALUE, COMPUTATION_ID));
        return makeFluxGreatAgain(preparedFlux, n, optimizationGoal);
    }

}
