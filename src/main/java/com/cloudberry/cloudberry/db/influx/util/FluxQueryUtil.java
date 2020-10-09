package com.cloudberry.cloudberry.db.influx.util;

import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import io.vavr.collection.Stream;

public abstract class FluxQueryUtil {

    public static Flux foldRestrictions(Flux query, Iterable<Restrictions> restrictions) {
        return Stream.ofAll(restrictions).foldLeft(query, Flux::filter);
    }

}
