package com.cloudberry.cloudberry.analytics.service.best;

import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationGoal;
import com.cloudberry.cloudberry.common.FluxUtils;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;

import java.util.Set;

import static com.cloudberry.cloudberry.db.influx.InfluxDefaults.Columns.TIME;
import static com.cloudberry.cloudberry.db.influx.InfluxDefaults.Columns.VALUE;
import static com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags.COMPUTATION_ID;

class BestComputationsByArea extends BestComputations {

    Flux getBest(int n,
                 OptimizationGoal optimizationGoal,
                 Restrictions restrictions,
                 String bucketName) {
        var preparedFlux = FluxUtils.epochQueryByComputationId(bucketName, restrictions)
                .keep(Set.of(TIME, VALUE, COMPUTATION_ID))
                .integral(); // gets integral from each group
        return makeFluxGreatAgain(preparedFlux, n, optimizationGoal);
    }

}
