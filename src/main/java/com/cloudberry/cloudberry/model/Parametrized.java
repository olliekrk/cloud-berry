package com.cloudberry.cloudberry.model;

import java.util.Map;
import java.util.Optional;

// to be used later with some utility methods for processing & extracting map entries by known keys
public interface Parametrized<K, V> {
    Map<K, V> getParameters();

    default Optional<V> getParameterOpt(K key) {
        return Optional.ofNullable(getParameters().get(key));
    }
}
