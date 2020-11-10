package com.cloudberry.cloudberry.common.syntax;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class SetSyntax {
    public static <T> Set<T> with(Collection<T> collection, T element) {
        return Stream.concat(Stream.of(element), collection.stream()).collect(Collectors.toUnmodifiableSet());
    }

    public static <T> Set<T> without(Collection<T> collection, T element) {
        return collection.stream().filter(e -> !e.equals(element)).collect(Collectors.toSet());
    }

    public static <T> Set<T> merge(Set<T> a, Set<T> b) {
        return Sets.newHashSet(Iterables.concat(a, b));
    }
}
