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

package io.parsingdata.metal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.cat;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.OneToManyValueExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.token.Token;

@RunWith(JUnit4.class)
public class ValueExpressionSemanticsTest {

    private final Token cat = seq(any("a"),
                                  any("b"),
                                  def("c", con(2), eq(cat(ref("a"), ref("b")))));

    @Test
    public void Cat() throws IOException {
        assertTrue(cat.parse(env(stream(1, 2, 1, 2))).isPresent());
    }

    @Test
    public void CatNoMatch() throws IOException {
        assertFalse(cat.parse(env(stream(1, 2, 12, 12))).isPresent());
    }

    @Test
    public void callback() throws IOException {
        final ParseState data = stream(1, 2, 3, 4);
        def("a", 4, eq(new OneToManyValueExpression(ref("a")) {
            @Override
            public Optional<Value> eval(Value value, ParseState parseState, Encoding encoding) {
                return Optional.of(value);
            }
        })).parse(env(data, enc()));
    }

}
