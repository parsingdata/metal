/*
 * Copyright 2013-2021 Netherlands Forensic Institute
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

    private final List<T> innerList;

    public ImmutableList() {
        innerList = unmodifiableList(new LinkedList<>());
    }

    public ImmutableList(final List<T> list) {
        innerList = unmodifiableList(new LinkedList<>(list));
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
        // make this of type ImmutableList? wrap it?
        return innerList.subList(fromIndex, toIndex);
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
}
