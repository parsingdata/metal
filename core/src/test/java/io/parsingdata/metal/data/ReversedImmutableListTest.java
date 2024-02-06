package io.parsingdata.metal.data;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
        final ImmutableList<Integer> reverse = new ImmutableList<>(originalToReverse).reverse().addHead(head);
        final List<Integer> actual = new ArrayList<>(reverse);
        assertIterableEquals(expected, actual);
    }
}