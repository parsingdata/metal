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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ImmutableList<T> extends LinkedList<T> {

    private Integer hashCode;

    public ImmutableList() {
        super();
    }

    public ImmutableList(final LinkedList<T> ts) {
        super(ts);
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
        final ImmutableList<T> ts = new ImmutableList<>(this);
        ts.addFirst(head);
        return ts;
    }

    public ImmutableList<T> addList(final ImmutableList<T> list) {
        final ImmutableList<T> ts = new ImmutableList<>(list);
        ts.addAll(this);
        return ts;
    }

    public T head() {
        if (isEmpty()) {
            return null;
        }
        return this.getFirst();
    }

    public ImmutableList<T> tail() {
        return new ImmutableList<>(this.subList(1, size()));
    }

    @Override
    public String toString() {
        return isEmpty() ? "" : ">" + head() + tail();
    }

    @Override
    public int hashCode() {
        if (hashCode == null) {
            hashCode = super.hashCode();
        }
        return hashCode;
    }

    public ImmutableList<T> reverse() {
        return new ReversedImmutableList<>(this);
    }

}
