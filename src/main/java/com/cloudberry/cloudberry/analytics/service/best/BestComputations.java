package com.cloudberry.cloudberry.analytics.service.best;

import com.cloudberry.cloudberry.analytics.model.optimization.OptimizationGoal;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;

import java.util.Set;

import static com.cloudberry.cloudberry.db.influx.InfluxDefaults.Columns.VALUE;
import static com.cloudberry.cloudberry.db.influx.InfluxDefaults.CommonTags.COMPUTATION_ID;

abstract class BestComputations {

    abstract Flux getBest(int n,
                          OptimizationGoal optimizationGoal,
                          Restrictions restrictions,
                          String bucketName);

    protected Flux makeFluxGreatAgain(Flux flux, int n, OptimizationGoal optimizationGoal) {
        var isDescendingBetter = OptimizationGoal.isMaximumSearched(optimizationGoal);
        return flux
                .group() // ungroups the data back into 1 table
                .sort(Set.of(VALUE), isDescendingBetter)
                .limit(n)
                .keep(Set.of(COMPUTATION_ID));
    }
}
