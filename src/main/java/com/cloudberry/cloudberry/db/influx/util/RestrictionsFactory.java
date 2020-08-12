package com.cloudberry.cloudberry.db.influx.util;

import com.influxdb.query.dsl.functions.restriction.Restrictions;

import java.util.Map;
import java.util.Optional;

public abstract class RestrictionsFactory {

    public static Restrictions measurement(String measurementName) {
        return Restrictions.measurement().equal(measurementName);
    }

    public static Optional<Restrictions> everyTag(Map<String, String> tags) {
        var tagRestrictions = tags
                .entrySet()
                .stream()
                .map(entry -> Restrictions.tag(entry.getKey()).equal(entry.getValue()))
                .toArray(Restrictions[]::new);

        return tagRestrictions.length == 0 ? Optional.empty() : Optional.of(Restrictions.and(tagRestrictions));
    }

    public static Optional<Restrictions> everyColumn(Map<String, Object> fields) {
        var fieldRestrictions = fields
                .entrySet()
                .stream()
                .map(entry -> Restrictions.column(entry.getKey()).equal(entry.getValue()))
                .toArray(Restrictions[]::new);

        return fieldRestrictions.length == 0 ? Optional.empty() : Optional.of(Restrictions.and(fieldRestrictions));
    }

    public static Optional<Restrictions> anyField(Map<String, Object> fields) {
        var fieldRestrictions = fields
                .entrySet()
                .stream()
                .map(entry -> Restrictions.and(
                        Restrictions.field().equal(entry.getKey()),
                        Restrictions.value().equal(entry.getValue())))
                .toArray(Restrictions[]::new);

        return fieldRestrictions.length == 0 ? Optional.empty() : Optional.of(Restrictions.or(fieldRestrictions));
    }

}
