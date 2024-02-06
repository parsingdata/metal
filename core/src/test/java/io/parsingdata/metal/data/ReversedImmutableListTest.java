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


    public static Stream<Arguments> getTest() {
        return Stream.of(
            arguments(List.of(1), 0, 1),
            arguments(List.of(1, 2), 0, 2),
            arguments(List.of(1, 2), 1, 1),
            arguments(List.of(1, 2, 3), 0, 3),
            arguments(List.of(1, 2, 3), 1, 2),
            arguments(List.of(1, 2, 3), 2, 1)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void getTest(final List<Integer> listToReverse, final int index, final Integer expected) {
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        assertEquals(expected, reverse.get(index));
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
    public void getOutOfBoundsTest(final List<Integer> listToReverse, final int index) {
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        assertThrows(IndexOutOfBoundsException.class, () -> reverse.get(index));
    }

    @Test
    public void toArrayTest() {
        final ImmutableList<Integer> reverse = new ImmutableList<>(List.of(1, 2, 3, 4, 5, 6)).reverse();
        assertArrayEquals(new Integer[]{6, 5, 4, 3, 2, 1}, reverse.toArray());
        assertArrayEquals(new Integer[]{6, 5, 4, 3, 2, 1}, reverse.toArray(new Integer[6]));
        assertArrayEquals(new Integer[]{6, 5, 4, 3, 2, 1}, reverse.toArray(new Integer[3]));
        assertArrayEquals(new Integer[]{6, 5, 4, 3, 2, 1, null, null, null}, reverse.toArray(new Integer[9]));
    }

    public static Stream<Arguments> headAndTailTest() {
        return Stream.of(
            arguments(List.of(), null, List.of()),
            arguments(List.of(1), 1, List.of()),
            arguments(List.of(1, 2), 2, List.of(1)),
            arguments(List.of(1, 2, 3), 3, List.of(2, 1)),
            arguments(List.of(1, 2, 3, 4), 4, List.of(3, 2, 1))
        );
    }

    @ParameterizedTest
    @MethodSource
    public void headAndTailTest(final List<Integer> originalToReverse, final Integer head, final List<Integer> tail) {
        final ImmutableList<Integer> reverse = new ImmutableList<>(originalToReverse).reverse();
        assertEquals(head, reverse.head());
        assertIterableEquals(tail, reverse.tail());
    }

    @Test
    public void reverseTest() {
        final List<Integer> listToReverse = List.of(1, 2, 3, 4, 5);
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        assertEquals(listToReverse, reverse.reverse());
    }

    @Test
    public void streamTest() {
        final List<Integer> listToReverse = List.of(1, 2, 3, 4, 5);
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        final Stream<Integer> actual = reverse.stream();
        assertEquals(List.of(5, 4, 3, 2, 1), actual.collect(Collectors.toList()));
    }

    @Test
    public void iteratorTest() {
        final List<Integer> listToReverse = List.of(1, 2, 3, 4, 5);
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        final Iterator<Integer> actual = reverse.iterator();
        assertEquals(5, actual.next());
        assertEquals(4, actual.next());
        assertEquals(3, actual.next());
        assertEquals(2, actual.next());
        assertEquals(1, actual.next());
        assertFalse(actual.hasNext());
    }

    @Test
    public void indexOfTest() {
        final List<Integer> listToReverse = List.of(1, 1, 2, 2, 3, 3);
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        assertEquals(4, reverse.indexOf(1));
        assertEquals(2, reverse.indexOf(2));
        assertEquals(0, reverse.indexOf(3));
    }


    @Test
    public void lastIndexOfTest() {
        final List<Integer> listToReverse = List.of(1, 1, 2, 2, 3, 3);
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        assertEquals(5, reverse.lastIndexOf(1));
        assertEquals(3, reverse.lastIndexOf(2));
        assertEquals(1, reverse.lastIndexOf(3));
    }

    @Test
    public void containsTest() {
        final List<Integer> listToReverse = List.of(1, 1, 2, 2, 3, 3);
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        assertTrue(reverse.contains(2));
        assertFalse(reverse.contains(5));
        assertFalse(reverse.contains(1L));
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
    public void containsAllTest(final boolean contains, final List<Integer> listToReverse, final List<Integer> list) {
        final ImmutableList<Integer> reverse = new ImmutableList<>(listToReverse).reverse();
        assertEquals(contains, reverse.containsAll(list));
    }

    public static Stream<Arguments> unsupportedOperationException() {
        return Stream.of(
            arguments((Consumer<ImmutableList<Integer>>) integers -> integers.set(0, 9)),
            arguments((Consumer<ImmutableList<Integer>>) integers -> integers.add(0, 9)),
            arguments((Consumer<ImmutableList<Integer>>) integers -> integers.remove(0)),
            arguments((Consumer<ImmutableList<Integer>>) integers -> integers.add( 9)),
            arguments((Consumer<ImmutableList<Integer>>) integers -> integers.remove(Integer.valueOf(9))), //
            arguments((Consumer<ImmutableList<Integer>>) integers -> integers.addAll(List.of(8, 9))),
            arguments((Consumer<ImmutableList<Integer>>) integers -> integers.addAll(0, List.of(8, 9))),
            arguments((Consumer<ImmutableList<Integer>>) integers -> integers.removeAll(List.of(1, 2))),
            arguments((Consumer<ImmutableList<Integer>>) integers -> integers.retainAll(List.of(1, 2))),
            arguments((Consumer<ImmutableList<Integer>>) integers -> integers.clear())
        );
    }

    @ParameterizedTest
    @MethodSource
    public void unsupportedOperationException(final Consumer<ImmutableList<Integer>> method) {
        final ImmutableList<Integer> reverse = new ImmutableList<>(List.of(1, 3, 2, 5, 4, 6)).reverse();
        assertThrows(UnsupportedOperationException.class, () -> method.accept(reverse));
    }

    public static Stream<Arguments> toStringTest() {
        return Stream.of(
            arguments(List.of(), ""),
            arguments(List.of(1), ">1"),
            arguments(List.of(1, 2), ">2>1"),
            arguments(List.of(1, 2, 3), ">3>2>1")
        );
    }

    @ParameterizedTest
    @MethodSource
    public void toStringTest(final List<Integer> listToReverse, final String expected) {
        assertEquals(expected, new ImmutableList<>(listToReverse).reverse().toString());
    }

    @Test
    public void listIteratorTest() {
        final ImmutableList<Integer> reverse = new ImmutableList<>(List.of(1, 2, 3)).reverse();
        final ListIterator<Integer> actual = reverse.listIterator();
        assertEquals(3, actual.next());
        assertEquals(2, actual.next());
        assertEquals(1, actual.next());
        assertFalse(actual.hasNext());
    }

    @Test
    public void listIteratorWithIndexTest() {
        final ImmutableList<Integer> reverse = new ImmutableList<>(List.of(1, 2, 3)).reverse();
        final ListIterator<Integer> actual2 = reverse.listIterator(2);
        assertEquals(1, actual2.next());
        assertFalse(actual2.hasNext());
    }

}