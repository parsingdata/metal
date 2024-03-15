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

package io.parsingdata.metal.expression.value;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Objects;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.encoding.Encoding;

public final class NotAValue extends ImmutableObject implements Value {

    public static final Value NOT_A_VALUE = new NotAValue();

    private NotAValue() {}

    @Override public Slice slice() { throw unsupported(); }

    @Override public Encoding encoding() { throw unsupported(); }

    @Override public byte[] value() { throw unsupported(); }

    @Override public BigInteger length() { throw unsupported(); }

    @Override public BigInteger asNumeric() { throw unsupported(); }

    @Override public String asString() { throw unsupported(); }

    @Override public BitSet asBitSet() { throw unsupported(); }

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("NOT_A_VALUE does not support any Value operation.");
    }

    @Override
    public String toString() {
        return "NOT_A_VALUE";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass());
    }

}
