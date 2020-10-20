package com.cloudberry.cloudberry.common.syntax;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class CollectionSyntax {
    /**
     * @param mutableCollectionSupplier supplier for a collection that must support Collection.addAll method
     */
    public static <E, R, C extends Collection<R>> C mapped(
            Collection<E> collection,
            Function<E, R> mapper,
            Supplier<C> mutableCollectionSupplier
    ) {
        return collection.stream()
                .map(mapper)
                .collect(Collectors.toCollection(mutableCollectionSupplier));
    }


    public static <T> Collection<T> flatten(Collection<Optional<T>> collection) {
        return collection.stream()
                .flatMap(Optional::stream)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    public static <T, C extends Collection<T>> C flatten(
            Collection<Optional<T>> collection,
            Supplier<C> mutableCollectionSupplier
    ) {
        return collection.stream()
                .flatMap(Optional::stream)
                .collect(Collectors.toCollection(mutableCollectionSupplier));
    }
}
