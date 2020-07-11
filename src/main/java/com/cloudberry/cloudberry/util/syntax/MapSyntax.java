package com.cloudberry.cloudberry.util.syntax;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MapSyntax {
    public static <K, V> Map<K, V> with(Map<K, V> map, K key, V value) {
        map.put(key, value);
        return map;
    }

    public static <K, V> Map<K, V> merged(Map<K, V>... maps) {
        return Arrays.stream(maps)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
