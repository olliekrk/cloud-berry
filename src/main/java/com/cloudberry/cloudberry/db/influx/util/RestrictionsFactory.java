package com.cloudberry.cloudberry.db.influx.util;

import com.influxdb.query.dsl.functions.restriction.Restrictions;
import io.vavr.collection.Stream;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public abstract class RestrictionsFactory {

    public static Restrictions measurement(String measurementName) {
        return Restrictions.measurement().equal(measurementName);
    }

    public static Optional<Restrictions> everyTagEquals(Map<String, String> tags) {
        var tagRestrictions = restrictionsFromMap(tags, RestrictionsFactory::tagEquals);
        return tagRestrictions.length == 0 ? Optional.empty() : Optional.of(Restrictions.and(tagRestrictions));
    }

    public static Optional<Restrictions> everyFieldEquals(Map<String, Object> fields) {
        var fieldRestrictions = restrictionsFromMap(fields, RestrictionsFactory::fieldEquals);
        return fieldRestrictions.length == 0 ? Optional.empty() : Optional.of(Restrictions.and(fieldRestrictions));
    }

    public static Optional<Restrictions> anyFieldEquals(Map<String, Object> fields) {
        var fieldRestrictions = restrictionsFromMap(fields, RestrictionsFactory::fieldEquals);
        return fieldRestrictions.length == 0 ? Optional.empty() : Optional.of(Restrictions.or(fieldRestrictions));
    }

    public static Restrictions fieldEquals(Map.Entry<String, Object> entry) {
        return fieldEquals(entry.getKey(), entry.getValue());
    }

    public static Restrictions fieldEquals(String key, Object value) {
        return Restrictions.and(Restrictions.field().equal(key), Restrictions.value().equal(value));
    }

    public static Restrictions hasField(String fieldName) {
        return Restrictions.field().equal(fieldName);
    }

    public static Restrictions hasTag(String tagName) {
        return Restrictions.tag(tagName).exists();
    }


    public static Optional<Restrictions> hasEveryTag(Set<String> tags) {
        var tagRestrictions = restrictionsFromSet(tags, RestrictionsFactory::hasTag);
        return tagRestrictions.length == 0 ? Optional.empty() : Optional.of(Restrictions.and(tagRestrictions));
    }

    public static Restrictions tagEquals(Map.Entry<String, String> tag) {
        return tagEquals(tag.getKey(), tag.getValue());
    }

    public static Restrictions tagEquals(String tagName, String tagValue) {
        return Restrictions.tag(tagName).equal(tagValue);
    }

    public static Restrictions tagIn(String tagName, Collection<String> tagValues) {
        var tagRestrictions = restrictionsFromSet(Set.copyOf(tagValues), tagValue -> tagEquals(tagName, tagValue));
        return Restrictions.or(tagRestrictions);
    }

    public static Restrictions everyRestriction(Iterable<Restrictions> restrictions) {
        return Restrictions.and(Stream.ofAll(restrictions).toJavaArray(Restrictions[]::new));
    }

    public static Restrictions anyRestriction(Iterable<Restrictions> restrictions) {
        return Restrictions.or(Stream.ofAll(restrictions).toJavaArray(Restrictions[]::new));
    }

    private static <T> Restrictions[] restrictionsFromMap(
            Map<String, T> restrictions,
            Function<Map.Entry<String, T>, Restrictions> createRestriction
    ) {
        return restrictions
                .entrySet()
                .stream()
                .map(createRestriction)
                .toArray(Restrictions[]::new);
    }

    private static Restrictions[] restrictionsFromSet(
            Set<String> restrictions,
            Function<String, Restrictions> createRestriction
    ) {
        return restrictions
                .stream()
                .map(createRestriction)
                .toArray(Restrictions[]::new);
    }

}
