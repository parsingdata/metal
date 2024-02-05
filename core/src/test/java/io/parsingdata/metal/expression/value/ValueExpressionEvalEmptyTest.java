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

package io.parsingdata.metal.expression.value;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.mod;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ImmutableList;

public class ValueExpressionEvalEmptyTest {

    @Test
    public void divisionByZero() {
        parse(div(con(1), con(0)));
    }

    @Test
    public void moduloNegative() {
        parse(mod(con(1), con(-1, signed())));
    }

    @Test
    public void moduloZero() {
        parse(mod(con(1), con(0)));
    }

    private void parse(final ValueExpression expression) {
        final ImmutableList<Value> result = expression.eval(stream(0), enc());
        assertEquals(1, (long) result.size());
        assertEquals(NOT_A_VALUE, result.head());
    }

}
