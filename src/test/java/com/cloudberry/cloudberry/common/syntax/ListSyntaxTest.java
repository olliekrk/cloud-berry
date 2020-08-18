package com.cloudberry.cloudberry.common.syntax;

import io.vavr.collection.Array;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ListSyntaxTest {

    @Test
    void averageLength_EmptyLists_Zero() {
        List<List<Object>> lists = List.of(List.of(), List.of());
        Assertions.assertEquals(ListSyntax.averageLength(lists), 0);
    }

    @Test
    void averageLength_NoLists_Zero() {
        List<List<Object>> lists = List.of();
        Assertions.assertEquals(ListSyntax.averageLength(lists), 0);
    }

    @Test
    void averageLength_SingleList_ListLength() {
        var lists = List.of(makeListOfLengthN(10));
        Assertions.assertEquals(ListSyntax.averageLength(lists), 10);
    }

    @Test
    void averageLength_MultipleLists_Average() {
        var lists = List.of(
                makeListOfLengthN(10),
                makeListOfLengthN(2),
                makeListOfLengthN(0)
        );
        Assertions.assertEquals(ListSyntax.averageLength(lists), 4);
    }

    private static List<Integer> makeListOfLengthN(int n) {
        return Array.fill(n, 0).toList().asJava();
    }
}
