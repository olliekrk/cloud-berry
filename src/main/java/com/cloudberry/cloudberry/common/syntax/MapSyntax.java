package com.cloudberry.cloudberry.common.syntax;


import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class MapSyntax {
    public static <K, V> Map<K, V> with(Map<K, V> map, K key, V value) {
        map.put(key, value);
        return map;
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

    public static <K, V, R> Map<K, R> zippedArrays(K[] keys, V[] values, Function<V, R> valuesMapper) {
        return IntStream.range(0, keys.length)
                .boxed()
                .collect(Collectors.toMap(i -> keys[i], i -> valuesMapper.apply(values[i])));
    }

    public static <K, V> Map<K, V> zippedArrays(K[] keys, V[] values) {
        return zippedArrays(keys, values, Function.identity());
    }
}
