package io.parsingdata.metal.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ReversedImmutableListTest {

    public static Stream<Arguments> addHeadTest() {
        return Stream.of(
            arguments(List.of(), 4, List.of(4)),
            arguments(List.of(1), 4, List.of(4, 1)),
            arguments(List.of(1, 2), 4, List.of(4, 2, 1)),
            arguments(List.of(1, 2, 3), 4, List.of(4, 3, 2, 1))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void addHeadTest(final List<Integer> originalToReverse, final int head, final List<Integer> expected) {
        final ImmutableList<Integer> reverse1 = new ImmutableList<>(originalToReverse).reverse();
        final ImmutableList<Integer> reverse = reverse1.addHead(head);
        final List<Integer> actual = new ArrayList<>(reverse);
        assertIterableEquals(expected, actual);
        assertEquals(head, reverse.head());
    }

    public static Stream<Arguments> addListTest() {
        return Stream.of(
            arguments(List.of(), List.of(), List.of()),
            arguments(List.of(1), List.of(), List.of(1)),
            arguments(List.of(1, 2), List.of(), List.of(2, 1)),
            arguments(List.of(1, 2, 3), List.of(), List.of(3, 2, 1)),

            arguments(List.of(), List.of(4), List.of(4)),
            arguments(List.of(1), List.of(4), List.of(1, 4)),
            arguments(List.of(1, 2), List.of(4), List.of(2, 1, 4)),
            arguments(List.of(1, 2, 3), List.of(4), List.of(3, 2, 1, 4)),

            arguments(List.of(), List.of(4, 5), List.of(4, 5)),
            arguments(List.of(1), List.of(4, 5), List.of(1, 4, 5)),
            arguments(List.of(1, 2), List.of(4, 5), List.of(2, 1, 4, 5)),
            arguments(List.of(1, 2, 3), List.of(4, 5), List.of(3, 2, 1, 4, 5))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void addListTest(final List<Integer> listToReverse, final List<Integer> listToAdd, final List<Integer> expected) {
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse().addList(new ImmutableList<>(listToAdd));
        final List<Integer> actual = new ArrayList<>(reverse);
        assertIterableEquals(expected, actual);
    }
}