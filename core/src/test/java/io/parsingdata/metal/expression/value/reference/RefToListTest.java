package io.parsingdata.metal.expression.value.reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import static io.parsingdata.metal.expression.value.reference.Ref.toList;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.parsingdata.metal.data.ImmutableList;

class RefToListTest {

    static Stream<Arguments> toListTest() {
        return Stream.of(
            arguments(ImmutableList.create(1), List.of(1)),
            arguments(ImmutableList.create(1).addHead(2), List.of(2, 1)),
            arguments(ImmutableList.create(1).addHead(2).addHead(3), List.of(3, 2, 1)),
            arguments(ImmutableList.create(new Integer[]{1, 2, 3}), List.of(1, 2, 3)),
            arguments(ImmutableList.create(new Integer[0]), List.of()),

            arguments(ImmutableList.create("1"), List.of("1")),
            arguments(ImmutableList.create("1").addHead("2"), List.of("2", "1")),
            arguments(ImmutableList.create("1").addHead("2").addHead("3"), List.of("3", "2", "1")),
            arguments(ImmutableList.create(new String[]{"1", "2", "3"}), List.of("1", "2", "3")),
            arguments(ImmutableList.create(new String[0]), List.of())
        );
    }

    @ParameterizedTest
    @MethodSource
    void toListTest(final ImmutableList<?> immutableList, final List<?> list) {
        assertEquals(list, toList(immutableList));
    }
}