package com.cloudberry.cloudberry.db.influx.util;

import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.util.List;

public abstract class FluxQueryUtil {

    public static Flux applyRestrictions(Flux query, List<Restrictions> restrictions) {
        return restrictions
                .stream()
                .map(r -> Tuple.of(r, query))
                .reduce((acc, next) -> Tuple.of(null, acc._2.filter(next._1)))
                .map(Tuple2::_2)
                .orElse(query);

    }

}
