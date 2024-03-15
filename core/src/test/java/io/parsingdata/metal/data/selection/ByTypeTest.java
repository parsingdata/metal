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

package io.parsingdata.metal.data.selection;

import static java.math.BigInteger.ZERO;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static io.parsingdata.metal.AutoEqualityTest.DUMMY_STREAM;
import static io.parsingdata.metal.data.ParseGraph.NONE;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.data.selection.ByType.getReferences;

import java.math.BigInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ParseReference;
import io.parsingdata.metal.data.Source;

public class ByTypeTest {

    public static final Source EMPTY_SOURCE = new Source() {
        @Override protected byte[] getData(BigInteger offset, BigInteger length) { throw new IllegalStateException(); }
        @Override protected boolean isAvailable(BigInteger offset, BigInteger length) { return false; }
        @Override public int immutableHashCode() { return -1; }
        @Override
        public boolean equals(Object obj) { return obj == this; }
    };

    @Test
    public void unresolvableRef() {
        final Exception e = Assertions.assertThrows(IllegalStateException.class, () ->
            getReferences(createFromByteStream(DUMMY_STREAM).createCycle(new ParseReference(ZERO, EMPTY_SOURCE, NONE)).order)
        );
        assertEquals("A ParseReference must point to an existing graph.", e.getMessage());
    }

}
