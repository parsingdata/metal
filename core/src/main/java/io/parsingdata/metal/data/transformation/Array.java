/*
 * Copyright 2013-2016 Netherlands Forensic Institute
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

package io.parsingdata.metal.data.transformation;

import static io.parsingdata.metal.Util.checkNotNull;

import io.parsingdata.metal.data.ImmutableList;

public final class Array {

    private Array() {}

    public static <T> ImmutableList<T> toList(final T[] array) {
        return toListRecursive(new ImmutableList<>(), checkNotNull(array, "array"), array.length - 1);
    }

    private static <T> ImmutableList<T> toListRecursive(final ImmutableList<T> list, final T[] array, final int index) {
        if (index < 0) { return list; }
        return toListRecursive(list.add(array[index]), array, index - 1);
    }

}
