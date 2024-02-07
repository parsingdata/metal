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

import static java.util.Collections.reverseOrder;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import static io.parsingdata.metal.Util.checkNotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Stream;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Util;


public class ImmutableList<T> extends ImmutableObject implements List<T> {

    final List<T> innerList;

    public ImmutableList() {
        innerList = List.of();
    }

    public ImmutableList(final List<T> list) {
        innerList = unmodifiableList(list);
    }

    public static <T> ImmutableList<T> create(final T head) {
        return new ImmutableList<T>().addHead(checkNotNull(head, "head"));
    }

    public static <T> ImmutableList<T> create(final T[] array) {
        return new ImmutableList<>(Arrays.stream(array).collect(toList()));
    }

    public ImmutableList<T> addHead(final T head) {
        final LinkedList<T> ts = new LinkedList<>(this);
        ts.addFirst(head);
        return new ImmutableList<>(ts);
    }

    public ImmutableList<T> addList(final ImmutableList<T> list) {
        final LinkedList<T> ts = new LinkedList<>(list);
        ts.addAll(this);
        return new ImmutableList<>(ts);
    }

    public T head() {
        if (isEmpty()) {
            return null;
        }
        return get(0);
    }

    /**
     * Returns this list without the head.
     * @return an immutable sublist
     * @deprecated Use {@link #stream()}, {@link #get(int)} or {@link #subList(int, int)} instead. This method is kept here for backward compatibility.
     */
    @Deprecated(since = "11.0.0")
    public ImmutableList<T> tail() {
        return new ImmutableList<>(subList(1, size()));
    }

    public boolean isEmpty() {
        return innerList.isEmpty();
    }

    public int size() {
        return innerList.size();
    }

    public boolean contains(Object value) {
        return innerList.contains(value);
    }

    public Stream<T> stream() {
        return innerList.stream();
    }

    public T get(int index) {
        return innerList.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return innerList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return innerList.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return innerList.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return innerList.listIterator(index);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return isEmpty() ? new ImmutableList<>() : innerList.subList(fromIndex, toIndex);
    }

    @Override
    public String toString() {
        return innerList.isEmpty() ? "" : ">" + head() + tail();
    }

    @Override
    public int immutableHashCode() {
        return innerList.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
                && Objects.equals(innerList, ((ImmutableList<?>)obj).innerList);
    }

    @Override
    public Iterator<T> iterator() {
        return innerList.iterator();
    }

    @Override
    public Object[] toArray() {
        return innerList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return innerList.toArray(a);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return innerList.containsAll(c);
    }

    @Override
    public T set(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, T element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(T t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    public ImmutableList<T> reverse() {
        return new ReversedImmutableList<>(this);
    }

    /**
     * The ReversedImmutableList returns a view of a reversed list on the specified list without coping it. This keeps memory usage low while
     * still keeping immutability. Copying is only applied when necessary. Reverting a ReversedImmutableList also returns the original list.
     *
     * @param <T> the type of the values in the list.
     */
    public static class ReversedImmutableList<T> extends ImmutableList<T> {

        /**
         * Constructor is private because it should be created using {@link ImmutableList#reverse()} instead.
         *
         * @param originalList the list to reverse
         */
        private ReversedImmutableList(List<T> originalList) {
            super(originalList);
        }

        @Override
        public T get(int index) {
            return innerList.get(size() - index - 1);
        }

        @Override
        public Object[] toArray() {
            return this.toArray(new Object[size()]);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            if (a.length < size())
                a = (T[])java.lang.reflect.Array.newInstance(
                    a.getClass().getComponentType(), size());

            Object[] result = a;
            for (int i = 0; i < size(); i++)
                result[size() - i - 1] = innerList.get(i);

            if (a.length > size())
                a[size()] = null;

            return a;
        }

        @Override
        public ImmutableList<T> addHead(final T head) {
            final LinkedList<T> ts2 = new LinkedList<>(innerList);
            ts2.addLast(head);
            return new ReversedImmutableList<>(ts2);
        }

        @Override
        public ImmutableList<T> addList(final ImmutableList<T> list) {
            final LinkedList<T> ts2 = new LinkedList<>(list.reverse());
            ts2.addAll(innerList);
            return new ReversedImmutableList<>(ts2);
        }

        @Override
        public T head() {
            return isEmpty() ? null : innerList.get(innerList.size() - 1);
        }

        @Override
        public ImmutableList<T> tail() {
            return isEmpty() ? new ImmutableList<>() : new ReversedImmutableList<>(innerList.subList(0, size() - 1));
        }

        @Override
        public Stream<T> stream() {
            return innerList.stream().sorted(reverseOrder());
        }

        @Override
        public Iterator<T> iterator() {
            return stream().iterator();
        }

        @Override
        public ListIterator<T> listIterator() {
            return unmodifiableList(stream().collect(toList())).listIterator();
        }

        @Override
        public ListIterator<T> listIterator(final int index) {
            return unmodifiableList(stream().collect(toList())).listIterator(index);
        }

        @Override
        public int indexOf(Object o) {
            return size() - super.lastIndexOf(o) - 1;
        }

        @Override
        public int lastIndexOf(Object o) {
            return size() - super.indexOf(o) - 1;
        }

        @Override
        public ImmutableList<T> reverse() {
            return new ImmutableList<>(innerList);
        }

        @Override
        public String toString() {
            return isEmpty() ? "" : ">" + head() + tail();
        }

        @Override
        public int immutableHashCode() {
            return stream().collect(toList()).hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            return Util.notNullAndSameClass(this, obj)
                && Objects.equals(innerList, ((ImmutableList<?>)obj).innerList);
        }
    }

}
