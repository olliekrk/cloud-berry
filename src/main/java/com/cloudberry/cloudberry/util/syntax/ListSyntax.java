package com.cloudberry.cloudberry.util.syntax;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class ListSyntax {
    public static <E, R> List<R> mapped(Collection<E> collection, Function<E, R> mapper) {
        return collection.stream().map(mapper).collect(Collectors.toList());
    }
}
