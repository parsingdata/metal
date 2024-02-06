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

import static io.parsingdata.metal.Util.checkNotNull;
import static java.util.Collections.unmodifiableList;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImmutableList<T> implements List<T> { // extends LinkedList<T> {

    private final List<T> innerList;

    private Integer hashCode;

    public ImmutableList() {
        innerList = unmodifiableList(new LinkedList<>());
    }

    public ImmutableList(final LinkedList<T> ts) {
        innerList = unmodifiableList(new LinkedList<>(ts));
    }

    public ImmutableList(final ImmutableList<T> ts) {
        this(ts.innerList);
    }

    public ImmutableList(List<T> collect) {
        this(new LinkedList<>(collect));
    }

    public static <T> ImmutableList<T> create(final T head) {
        return new ImmutableList<T>().addHead(checkNotNull(head, "head"));
    }

    public static <T> ImmutableList<T> create(final T[] array) {
        return new ImmutableList<>(new LinkedList<>(Arrays.stream(array).collect(Collectors.toList())));
    }

    public ImmutableList<T> addHead(final T head) {
        final LinkedList<T> ts = new LinkedList<>(innerList);
        ts.addFirst(head);
        return new ImmutableList<>(ts);
    }

    public ImmutableList<T> addList(final ImmutableList<T> list) {
        final LinkedList<T> ts = new LinkedList<>(list.innerList);
        ts.addAll(this.innerList);
        return new ImmutableList<>(ts);
    }

    public T head() {
        if (innerList.isEmpty()) {
            return null;
        }
        return innerList.get(0);
    }

    public ImmutableList<T> tail() {
        return new ImmutableList<>(innerList.subList(1, innerList.size()));
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
    public int hashCode() {
        if (hashCode == null) {
            hashCode = super.hashCode();
        }
        return hashCode;
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
    public <T1> T1[] toArray(T1[] a) {
        return innerList.toArray(a);
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
    public boolean containsAll(Collection<?> c) {
        return innerList.containsAll(c);
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
}
