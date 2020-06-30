package com.cloudberry.cloudberry.util.syntax;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class SetSyntax {
    public static <T> Set<T> with(Set<T> set, T element) {
        return Stream.concat(Stream.of(element), set.stream()).collect(Collectors.toUnmodifiableSet());
    }
}
