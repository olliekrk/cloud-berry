package com.cloudberry.cloudberry.model;

import java.util.Map;

// to be used later with some utility methods for processing & extracting map entries by known keys
public interface Parametrized<K, V> {
    Map<K, V> getParameters();
}
