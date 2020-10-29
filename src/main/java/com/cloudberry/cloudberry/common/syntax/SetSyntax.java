package com.cloudberry.cloudberry.common.syntax;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class SetSyntax {
    public static <T> Set<T> with(Set<T> set, T element) {
        return Stream.concat(Stream.of(element), set.stream()).collect(Collectors.toUnmodifiableSet());
    }

    public static <T> Set<T> merge(Set<T> a, Set<T> b) {
        return Sets.newHashSet(Iterables.concat(a, b));
    }
}
