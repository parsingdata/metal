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
import java.util.stream.Collectors;

public class ImmutableList<T> extends LinkedList<T> {

    public ImmutableList() {
        super();
    }

    public ImmutableList(final LinkedList<T> ts) {
        super(ts);
    }

    public static <T> ImmutableList<T> create(final T head) {
        return new ImmutableList<T>().addHead(checkNotNull(head, "head"));
    }

    public static <T> ImmutableList<T> create(final T[] array) {
        return new ImmutableList<>(new LinkedList<>(Arrays.stream(array).collect(Collectors.toList())));
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
        return this.getFirst();
    }

    public ImmutableList<T> tail() {
        final LinkedList<T> ts = new LinkedList<>(this.subList(1, size()));
        return new ImmutableList<>(ts);
    }

    @Override
    public String toString() {
        return isEmpty() ? "" : ">" + head() + tail();
    }

    // TODO cache the hashcode like in ImmutableObject.

}
