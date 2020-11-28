package com.cloudberry.cloudberry.common.syntax;


import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class MapSyntax {
    public static <K, V> Map<K, V> with(Map<K, V> map, K key, V value) {
        return ImmutableMap.<K, V>builder()
                .putAll(map)
                .put(key, value)
                .build();
    }

    public static <K, V> Map<K, V> merged(Map<K, V>... maps) {
        return Arrays.stream(maps)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v, v2) -> v2));
    }

    public static <K, V> Map<K, V> getNewParamsMap(
            Map<K, V> newParams,
            Map<K, V> prevParams,
            boolean overrideParams
    ) {
        return overrideParams ? newParams : merged(prevParams, newParams);
    }

    public static <K, V, R> Map<K, R> zippedArrays(K[] keys, V[] values, BiFunction<K, V, R> valuesMapper) {
        return IntStream.range(0, keys.length)
                .boxed()
                .collect(Collectors.toMap(i -> keys[i], i -> valuesMapper.apply(keys[i], values[i])));
    }

}
