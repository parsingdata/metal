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

import io.parsingdata.metal.data.*;
import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.encoding.Sign;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.StructSink;
import io.parsingdata.metal.token.Token;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static io.parsingdata.metal.Shorthand.*;
import static io.parsingdata.metal.data.OptionalValueList.EMPTY;
import static io.parsingdata.metal.data.ParseGraph.NONE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static org.junit.Assert.assertEquals;

public class ToStringTest {

    private static final String prefix = "prefix";
    private int count;

    private static final StructSink sink = new StructSink() {
        @Override
        public void handleStruct(String scopeName, Environment env, Encoding enc, ParseGraph struct) {
        }
    };

    @Before
    public void before() {
        count = 0;
    }

    @Test
    public void validateToStringImplementation() {
        final Expression e = not(and(eq(v(), v()), or(eqNum(v()), and(eqStr(v()), or(gtNum(v()), ltNum(v()))))));
        final Token t = repn(sub(opt(pre(str("str", rep(cho(str("str", any(n())), seq(nod(v()), whl(def(n(), con(1), e), e)))), sink), e)), v()), v());
        final String output = t.toString();
        for (int i = 0; i < count; i++) {
            Assert.assertTrue(output.contains(prefix + i));
        }
    }

    private String n() {
        return prefix + count++;
    }

    private Token t() { return any("a"); }

    private ValueExpression v() {
        return neg(add(div(mod(mul(sub(last(ref(n())), first(ref(n()))), con(1)), cat(ref(n()), ref(t()))), add(self, add(offset(ref(n())), add(currentOffset, count(ref(n())))))), elvis(ref(n()), ref(n()))));
    }

    @Test
    public void tokensWithArrays() {
        final Token a = def("_name_a_", con(1));
        final Token b = def("_name_b_", con(2));
        final Token c = def("_name_c_", con(1));
        final Token s1 = seq(a, b, c);
        checkToken(s1);
        final Token c1 = cho(c, b, a);
        checkToken(c1);
    }

    private void checkToken(final Token t) {
        final String s1s = t.toString();
        Assert.assertTrue(s1s.contains("_name_a_"));
        Assert.assertTrue(s1s.contains("_name_b_"));
        Assert.assertTrue(s1s.contains("_name_c_"));
    }

    @Test
    public void encoding() {
        assertEquals("Encoding(UNSIGNED,US-ASCII,BIG_ENDIAN)", new Encoding().toString());
        assertEquals("Encoding(SIGNED,US-ASCII,BIG_ENDIAN)", new Encoding(Sign.SIGNED).toString());
        assertEquals("Encoding(UNSIGNED,UTF-8,BIG_ENDIAN)", new Encoding(StandardCharsets.UTF_8).toString());
        assertEquals("Encoding(UNSIGNED,US-ASCII,LITTLE_ENDIAN)", new Encoding(ByteOrder.LITTLE_ENDIAN).toString());
    }

    @Test
    public void data() {
        final Environment environment = stream(1, 2);
        final String envString = "stream: InMemoryByteStream(2); offset: 0; order: graph(EMPTY)";
        assertEquals(envString, environment.toString());
        final ParseResult result = new ParseResult(true, environment);
        assertEquals("ParseResult(true, " + environment + ")", result.toString());
        final ParseValue pv1 = new ParseValue("name", NONE, 0, new byte[] { 1, 2 }, enc());
        final String pv1String = "name(0x0102)";
        final OptionalValue ov1 = OptionalValue.of(pv1);
        final OptionalValue ov2 = OptionalValue.of(new Value(new byte[] { 3 }, enc()));
        assertEquals(">OptionalValue(0x03)>OptionalValue(" + pv1String + ")", EMPTY.add(ov1).add(ov2).toString());
        final ParseValue pv2 = new ParseValue("two", NONE, 0, new byte[] { 3, 4 }, enc());
        final String pv2String = "two(0x0304)";
        assertEquals(">" + pv2String + ">" + pv1String, ParseItemList.create(pv1).add(pv2).toString());
        assertEquals(">" + pv2String + ">" + pv1String, ParseValueList.create(pv1).add(pv2).toString());
    }

}
