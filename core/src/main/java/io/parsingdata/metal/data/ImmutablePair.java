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

import java.util.Objects;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Util;

public class ImmutablePair<L, R> extends ImmutableObject {

    public final L left;
    public final R right;

    public ImmutablePair(final L left, final R right) {
        this.left = checkNotNull(left, "left");
        this.right = checkNotNull(right, "right");
    }

    @Override
    public String toString() {
        return left + "->" + right;
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(left, ((ImmutablePair<?, ?>)obj).left)
            && Objects.equals(right, ((ImmutablePair<?, ?>)obj).right);
    }

    @Override
    public int cachingHashCode() {
        return Objects.hash(getClass(), left, right);
    }

}
