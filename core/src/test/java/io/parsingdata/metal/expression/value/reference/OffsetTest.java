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

package io.parsingdata.metal.expression.value.reference;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.offset;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.expression.value.Value;

public class OffsetTest {

    @Test
    public void definedValueOffset() {
        final ImmutableList<Value> offsetCon = offset(con(1)).eval(stream(), enc());
        assertEquals(1, (long) offsetCon.size());
        assertEquals(0, offsetCon.head().asNumeric().intValueExact());
    }

}
