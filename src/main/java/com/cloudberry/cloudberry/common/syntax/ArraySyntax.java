package com.cloudberry.cloudberry.common.syntax;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

public abstract class ArraySyntax {
    public static <T> boolean has(T[] array, T element) {
        return Arrays.asList(array).contains(element);
    }

    public static <T> LinkedList<T> linkedList(T[] array) {
        return Arrays.stream(array).collect(Collectors.toCollection(LinkedList::new));
    }
}
