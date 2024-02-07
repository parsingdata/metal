/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.parsingdata.metal.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Note: the same tests are performed at {@link ReversedImmutableListTest}. Updates in this class may also be valuable there.
 */
public class ImmutableListTest {

    public static Stream<Arguments> addHeadTest() {
        return Stream.of(
            arguments(List.of(), 4, List.of(4)),
            arguments(List.of(1), 4, List.of(4, 1)),
            arguments(List.of(1, 2), 4, List.of(4, 1, 2)),
            arguments(List.of(1, 2, 3), 4, List.of(4, 1, 2, 3))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void addHeadTest(final List<Integer> list, final int head, final List<Integer> expected) {
        final ImmutableList<Integer> immutableList = new ImmutableList<>(list).addHead(head);
        final List<Integer> actual = new ArrayList<>(immutableList);
        assertIterableEquals(expected, actual);
        assertEquals(head, immutableList.head());
    }

    public static Stream<Arguments> addListTest() {
        return Stream.of(
            arguments(List.of(), List.of(), List.of()),
            arguments(List.of(1), List.of(), List.of(1)),
            arguments(List.of(1, 2), List.of(), List.of(1, 2)),
            arguments(List.of(1, 2, 3), List.of(), List.of(1, 2, 3)),

            arguments(List.of(), List.of(4), List.of(4)),
            arguments(List.of(1), List.of(4), List.of(4, 1)),
            arguments(List.of(1, 2), List.of(4), List.of(4, 1, 2)),
            arguments(List.of(1, 2, 3), List.of(4), List.of(4, 1, 2, 3)),

            arguments(List.of(), List.of(4, 5), List.of(4, 5)),
            arguments(List.of(1), List.of(4, 5), List.of(4, 5, 1)),
            arguments(List.of(1, 2), List.of(4, 5), List.of(4, 5, 1, 2)),
            arguments(List.of(1, 2, 3), List.of(4, 5), List.of(4, 5, 1, 2, 3))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void addListTest(final List<Integer> list, final List<Integer> listToAdd, final List<Integer> expected) {
        final ImmutableList<Integer> immutableList = new ImmutableList<>(list).addList(new ImmutableList<>(listToAdd));
        final List<Integer> actual = new ArrayList<>(immutableList);
        assertIterableEquals(expected, actual);
    }

    public static Stream<Arguments> getTest() {
        return Stream.of(
            arguments(List.of(1), 0, 1),
            arguments(List.of(1, 2), 0, 1),
            arguments(List.of(1, 2), 1, 2),
            arguments(List.of(1, 2, 3), 0, 1),
            arguments(List.of(1, 2, 3), 1, 2),
            arguments(List.of(1, 2, 3), 2, 3)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getTest(final List<Integer> list, final int index, final Integer expected) {
        final ImmutableList<Integer> immutableList = new ImmutableList<>(list);
        assertEquals(expected, immutableList.get(index));
    }


    public static Stream<Arguments> getOutOfBoundsTest() {
        return Stream.of(
            arguments(List.of(), -1),
            arguments(List.of(), 0),
            arguments(List.of(), 1),

            arguments(List.of(1), -1),
            arguments(List.of(1), 1),

            arguments(List.of(1, 2), -1),
            arguments(List.of(1, 2), 2),

            arguments(List.of(1, 2, 3), -1),
            arguments(List.of(1, 2, 3), 3)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getOutOfBoundsTest(final List<Integer> list, final int index) {
        final ImmutableList<Integer> immutableList = new ImmutableList<>(list);
        assertThrows(IndexOutOfBoundsException.class, () -> immutableList.get(index));
    }

    @Test
    public void toArrayTest() {
        final ImmutableList<Integer> immutableList = new ImmutableList<>(List.of(1, 2, 3, 4, 5, 6));
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6}, immutableList.toArray());
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6}, immutableList.toArray(new Integer[6]));
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6}, immutableList.toArray(new Integer[3]));
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5, 6, null, null, null}, immutableList.toArray(new Integer[9]));
    }

    public static Stream<Arguments> headAndTailTest() {
        return Stream.of(
            arguments(List.of(), null, List.of()),
            arguments(List.of(1), 1, List.of()),
            arguments(List.of(1, 2), 1, List.of(2)),
            arguments(List.of(1, 2, 3), 1, List.of(2, 3)),
            arguments(List.of(1, 2, 3, 4), 1, List.of(2, 3, 4))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void headAndTailTest(final List<Integer> list, final Integer head, final List<Integer> tail) {
        final ImmutableList<Integer> immutableList = new ImmutableList<>(list);
        assertEquals(head, immutableList.head());
        assertIterableEquals(tail, immutableList.tail());
    }

    @Test
    public void reverseTest() {
        final List<Integer> listToReverse = List.of(1, 2, 3, 4, 5);
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse);
        assertIterableEquals(List.of(5, 4, 3, 2, 1), reverse.reverse());
    }

    @Test
    public void streamTest() {
        final List<Integer> list = List.of(1, 2, 3, 4, 5);
        final ImmutableList<Integer> immutableList = new ImmutableList<>(list);
        final Stream<Integer> actual = immutableList.stream();
        assertEquals(List.of(1, 2, 3, 4, 5), actual.collect(Collectors.toList()));
    }

    @Test
    public void iteratorTest() {
        final List<Integer> list = List.of(1, 2, 3, 4, 5);
        final ImmutableList<Integer> immutableList = new ImmutableList<>(list);
        final Iterator<Integer> actual = immutableList.iterator();
        assertEquals(1, actual.next());
        assertEquals(2, actual.next());
        assertEquals(3, actual.next());
        assertEquals(4, actual.next());
        assertEquals(5, actual.next());
        assertFalse(actual.hasNext());
    }

    @Test
    public void indexOfTest() {
        final List<Integer> list = List.of(1, 1, 2, 2, 3, 3);
        final ImmutableList<Integer> immutableList = new ImmutableList<>(list);
        assertEquals(0, immutableList.indexOf(1));
        assertEquals(2, immutableList.indexOf(2));
        assertEquals(4, immutableList.indexOf(3));
    }


    @Test
    public void lastIndexOfTest() {
        final List<Integer> list = List.of(1, 1, 2, 2, 3, 3);
        final ImmutableList<Integer> immutableList = new ImmutableList<>(list);
        assertEquals(1, immutableList.lastIndexOf(1));
        assertEquals(3, immutableList.lastIndexOf(2));
        assertEquals(5, immutableList.lastIndexOf(3));
    }

    @Test
    public void containsTest() {
        final List<Integer> list = List.of(1, 1, 2, 2, 3, 3);
        final ImmutableList<Integer> immutableList = new ImmutableList<>(list);
        assertTrue(immutableList.contains(2));
        assertFalse(immutableList.contains(5));
        assertFalse(immutableList.contains(1L));
    }

    public static Stream<Arguments> containsAllTest() {
        return Stream.of(
            arguments(true, List.of(), List.of()),
            arguments(false, List.of(), List.of(1)),
            arguments(false, List.of(), List.of(1, 2)),

            arguments(true, List.of(1), List.of()),
            arguments(true, List.of(1), List.of(1)),
            arguments(false, List.of(1), List.of(2)),
            arguments(false, List.of(1), List.of(1, 2)),

            arguments(true, List.of(1, 2), List.of()),
            arguments(true, List.of(1, 2), List.of(1)),
            arguments(true, List.of(1, 2), List.of(2)),
            arguments(true, List.of(1, 2), List.of(1, 2)),
            arguments(false, List.of(1, 2), List.of(1, 2, 3)),

            arguments(true, List.of(1, 2, 3), List.of()),
            arguments(true, List.of(1, 2, 3), List.of(1)),
            arguments(true, List.of(1, 2, 3), List.of(2)),
            arguments(true, List.of(1, 2, 3), List.of(3)),
            arguments(true, List.of(1, 2, 3), List.of(1, 2)),
            arguments(true, List.of(1, 2, 3), List.of(2, 3)),
            arguments(true, List.of(1, 2, 3), List.of(3, 1)),
            arguments(true, List.of(1, 2, 3), List.of(3, 2)),
            arguments(true, List.of(1, 2, 3), List.of(1, 2, 3)),

            arguments(false, List.of(1, 2, 3), List.of(4)),
            arguments(false, List.of(1, 2, 3), List.of(4, 1)),
            arguments(false, List.of(1, 2, 3), List.of(4, 5, 1, 2, 3))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void containsAllTest(final boolean contains, final List<Integer> list, final List<Integer> listToTest) {
        final ImmutableList<Integer> immutableList = new ImmutableList<>(list);
        assertEquals(contains, immutableList.containsAll(listToTest));
    }

    // All methods that modifies the list should throw an UnsupportedOperationException.
    public static Stream<Arguments> unsupportedOperationException() {
        return Stream.of(
            arguments(unsupported(list -> list.add(0, 9))),
            arguments(unsupported(list -> list.add( 9))),
            arguments(unsupported(list -> list.set(0, 9))),
            arguments(unsupported(list -> list.remove(0))),
            arguments(unsupported(list -> list.remove(Integer.valueOf(9)))),
            arguments(unsupported(list -> list.addAll(List.of(8, 9)))),
            arguments(unsupported(list -> list.addAll(0, List.of(8, 9)))),
            arguments(unsupported(list -> list.removeAll(List.of(1, 2)))),
            arguments(unsupported(list -> list.retainAll(List.of(1, 2)))),
            arguments(unsupported(list -> list.clear())),
            arguments(unsupported(list -> list.iterator().remove())),
            arguments(unsupported(list -> list.listIterator().add(0))),
            arguments(unsupported(list -> list.listIterator().set(0))),
            arguments(unsupported(list -> list.listIterator().remove()))
        );
    }

    private static Consumer<ImmutableList<Integer>> unsupported(final Consumer<ImmutableList<Integer>> immutableListConsumer) {
        return immutableListConsumer;
    }

    @ParameterizedTest
    @MethodSource
    public void unsupportedOperationException(final Consumer<ImmutableList<Integer>> method) {
        final ImmutableList<Integer> immutableList = new ImmutableList<>(List.of(1, 3, 2, 5, 4, 6));
        assertThrows(UnsupportedOperationException.class, () -> method.accept(immutableList));
    }

    public static Stream<Arguments> toStringTest() {
        return Stream.of(
            arguments(List.of(), ""),
            arguments(List.of(1), ">1"),
            arguments(List.of(1, 2), ">1>2"),
            arguments(List.of(1, 2, 3), ">1>2>3")
        );
    }

    @ParameterizedTest
    @MethodSource
    public void toStringTest(final List<Integer> list, final String expected) {
        assertEquals(expected, new ImmutableList<>(list).toString());
    }

    @Test
    public void listIteratorTest() {
        final ImmutableList<Integer> immutableList = new ImmutableList<>(List.of(1, 2, 3));
        final ListIterator<Integer> actual = immutableList.listIterator();
        assertEquals(1, actual.next());
        assertEquals(2, actual.next());
        assertEquals(3, actual.next());
        assertFalse(actual.hasNext());
    }

    @Test
    public void listIteratorWithIndexTest() {
        final ImmutableList<Integer> reverse = new ImmutableList<>(List.of(1, 2, 3));
        final ListIterator<Integer> actual = reverse.listIterator(2);
        assertEquals(3, actual.next());
        assertFalse(actual.hasNext());
    }

}
