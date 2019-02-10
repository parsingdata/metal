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

package io.parsingdata.metal.expression.value;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Objects;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.encoding.Encoding;

public class NotAValue implements Value {

    public static final Value NOT_A_VALUE = new NotAValue();
    private static final UnsupportedOperationException UNSUPPORTED = new UnsupportedOperationException("NOT_A_VALUE does not support any Value operation.");

    private NotAValue() {}

    @Override public Slice getSlice() { throw UNSUPPORTED; }
    @Override public Encoding getEncoding() { throw UNSUPPORTED; }
    @Override public byte[] getValue() { throw UNSUPPORTED; }
    @Override public BigInteger getLength() { throw UNSUPPORTED; }
    @Override public BigInteger asNumeric() { throw UNSUPPORTED; }
    @Override public String asString() { throw UNSUPPORTED; }
    @Override public BitSet asBitSet() { throw UNSUPPORTED; }

    @Override
    public String toString() {
        return "NOT_A_VALUE";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass());
    }

}
