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

package io.parsingdata.metal;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.AutoEqualityTest.DUMMY_STREAM;
import static io.parsingdata.metal.Shorthand.TRUE;
import static io.parsingdata.metal.Shorthand.and;
import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqStr;
import static io.parsingdata.metal.Shorthand.gtEqNum;
import static io.parsingdata.metal.Shorthand.gtNum;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ltEqNum;
import static io.parsingdata.metal.Shorthand.ltNum;
import static io.parsingdata.metal.Shorthand.mapLeft;
import static io.parsingdata.metal.Shorthand.mapRight;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.Shorthand.when;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.expression.value.ExpandTest.createParseValue;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.Selection;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.token.Cho;
import io.parsingdata.metal.token.Seq;
import io.parsingdata.metal.token.Token;

public class ShorthandsTest {

    private static final Token multiSequence =
        seq(def("a", con(1), eq(con(1))),
            def("b", con(1), eq(con(2))),
            def("c", con(1), eq(con(3))));

    @Test
    public void sequenceMultiMatch() {
        assertTrue(multiSequence.parse(env(stream(1, 2, 3))).isPresent());
    }

    @Test
    public void sequenceMultiNoMatch() {
        assertFalse(multiSequence.parse(env(stream(1, 2, 2))).isPresent());
    }

    private static final Token multiChoice =
        cho(def("a", con(1), gtNum(con(2))),
            def("b", con(1), gtNum(con(1))),
            def("c", con(1), gtNum(con(0))));

    @Test
    public void choiceMultiMatchA() {
        runChoice(3, "a");
    }

    @Test
    public void choiceMultiMatchB() {
        runChoice(2, "b");
    }

    @Test
    public void choiceMultiMatchC() {
        runChoice(1, "c");
    }

    private void runChoice(final int data, final String matched) {
        final Optional<ParseState> result = multiChoice.parse(env(stream(data)));
        assertTrue(result.isPresent());
        assertTrue(result.get().order.current().get().matches(matched));
    }

    @Test
    public void choiceMultiNoMatch() {
        assertFalse(multiChoice.parse(env(stream(0))).isPresent());
    }

    private static final Token nonLocalCompare =
        seq(any("a"),
            def("b", con(3)),
            def("c", con(1), and(
                                 and(
                                     and(
                                         gtNum(last(ref("a")), con(0)),
                                         gtEqNum(last(ref("a")), con(0))),
                                     ltNum(con(0), last(ref("a")))),
                                 ltEqNum(con(0), last(ref("a"))))),
            def("d", con(1), eqStr(last(ref("b")), con("abc")))
        );

    @Test
    public void nonLocalCompare() {
        assertTrue(nonLocalCompare.parse(env(stream(1, 'a', 'b', 'c', 0, 0))).isPresent());
    }

    @Test
    public void allTokensNamed() {
        final Optional<ParseState> result =
            when("when",
                rep("rep",
                    repn("repn",
                        seq("seq",
                            pre("pre",
                                opt("opt",
                                    any("a")), TRUE),
                            cho("cho",
                                def("def0", con(1), eq(con(0))),
                                def("def1", con(1), eq(con(1)))),
                            sub("sub",
                                def("def2", con(1), eq(con(2))),
                                con(2)),
                            tie("tie",
                                def("def3", con(1), eq(con(1))),
                                last(ref("def1")))
                        ), con(1)
                    )
                ), TRUE).parse(env(stream(2, 1, 2)));
        assertTrue(result.isPresent());
        checkNameAndValue("when.rep.repn.seq.pre.opt.a", 2, result.get());
        checkNameAndValue("when.rep.repn.seq.cho.def1", 1, result.get());
        checkNameAndValue("when.rep.repn.seq.sub.def2", 2, result.get());
        checkNameAndValue("when.rep.repn.seq.tie.def3", 1, result.get());
    }

    private void checkNameAndValue(final String name, final int value, final ParseState parseState) {
        ImmutableList<Value> optionalValues = ref(name).eval(parseState, enc());
        assertEquals(1, (long) optionalValues.size());
        assertEquals(value, optionalValues.head().asNumeric().intValueExact());

        ImmutableList<Value> values = optionalValues;
        while (!values.isEmpty()) {
            final Value current = values.head();
            assertThat(current, is(instanceOf(ParseValue.class)));
            assertEquals(name, ((ParseValue) values.head()).name);
            values = values.tail();
        }
    }

    public static final Token DEF_A = any("a");
    public static final Token DEF_B = any("b");

    @Test
    public void checkChoTokens() {
        final Token choToken = cho(DEF_A, DEF_B);
        final Cho cho = (Cho)choToken;
        assertEquals(2, (long) cho.tokens.size());
        assertEquals(DEF_A, cho.tokens.head());
        assertEquals(DEF_B, cho.tokens.tail().head());
    }

    @Test
    public void checkSeqTokens() {
        final Token seqToken = seq(DEF_A, DEF_B);
        final Seq seq = (Seq)seqToken;
        assertEquals(2, (long) seq.tokens.size());
        assertEquals(DEF_A, seq.tokens.head());
        assertEquals(DEF_B, seq.tokens.tail().head());
    }

    private final ParseState PARSE_STATE = createFromByteStream(DUMMY_STREAM).add(createParseValue("a", 126)).add(createParseValue("a", 84)).add(createParseValue("a", 42));

    @Test
    public void mapLeftWithSub() {
        ImmutableList<Value> result = mapLeft(Shorthand::sub, ref("a"), con(2)).eval(PARSE_STATE, enc());
        assertEquals(3, (long) result.size());
        for (int i = 0; i < 3; i++) {
            assertEquals((i * 42) + 40, result.head().asNumeric().intValueExact());
            result = result.tail();
        }
    }

    @Test
    public void mapRightWithSub() {
        ImmutableList<Value> result = mapRight(Shorthand::sub, con(126), ref("a")).eval(PARSE_STATE, enc());
        assertEquals(3, (long) result.size());
        for (int i = 0; i < 3; i++) {
            assertEquals(((3 - i) * 42) - 42, result.head().asNumeric().intValueExact());
            result = result.tail();
        }
    }

    @Test
    public void whenTrue() {
        Optional<ParseState> result = when(def("name", con(1), eq(con(1))), TRUE).parse(env(stream(1)));
        assertTrue(result.isPresent());
        assertEquals(1, result.get().offset.intValueExact());
        ImmutableList<ParseValue> parseValues = Selection.getAllValues(result.get().order, parseValue -> parseValue.matches("name") && parseValue.value().length == 1 && parseValue.value()[0] == 1);
        assertEquals(1, (long) parseValues.size());
    }

    @Test
    public void whenFalse() {
        Optional<ParseState> result =
            seq(
                when(def("name1", con(1), eq(con(1))), not(TRUE)),
                def("name2", con(1), eq(con(2)))).parse(env(stream(2)));
        assertTrue(result.isPresent());
        assertEquals(1, result.get().offset.intValueExact());
        ImmutableList<ParseValue> parseValues = Selection.getAllValues(result.get().order, parseValue -> parseValue.matches("name2") && parseValue.value().length == 1 && parseValue.value()[0] == 2);
        assertEquals(1, (long) parseValues.size());
    }

}
