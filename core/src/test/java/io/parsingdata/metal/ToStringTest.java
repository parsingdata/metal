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

package io.parsingdata.metal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.CURRENT_ITERATION;
import static io.parsingdata.metal.Shorthand.CURRENT_OFFSET;
import static io.parsingdata.metal.Shorthand.SELF;
import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.and;
import static io.parsingdata.metal.Shorthand.bytes;
import static io.parsingdata.metal.Shorthand.cat;
import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.count;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.elvis;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.eqStr;
import static io.parsingdata.metal.Shorthand.exp;
import static io.parsingdata.metal.Shorthand.first;
import static io.parsingdata.metal.Shorthand.fold;
import static io.parsingdata.metal.Shorthand.foldLeft;
import static io.parsingdata.metal.Shorthand.foldRight;
import static io.parsingdata.metal.Shorthand.gtEqNum;
import static io.parsingdata.metal.Shorthand.gtNum;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ltEqNum;
import static io.parsingdata.metal.Shorthand.ltNum;
import static io.parsingdata.metal.Shorthand.mod;
import static io.parsingdata.metal.Shorthand.mul;
import static io.parsingdata.metal.Shorthand.neg;
import static io.parsingdata.metal.Shorthand.nod;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.nth;
import static io.parsingdata.metal.Shorthand.offset;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.or;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.rev;
import static io.parsingdata.metal.Shorthand.scope;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.Shorthand.token;
import static io.parsingdata.metal.Shorthand.until;
import static io.parsingdata.metal.Shorthand.whl;
import static io.parsingdata.metal.data.ParseGraph.NONE;
import static io.parsingdata.metal.data.Slice.createFromBytes;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ImmutablePair;
import io.parsingdata.metal.data.ParseReference;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.callback.Callback;
import io.parsingdata.metal.data.callback.Callbacks;
import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.encoding.Sign;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.value.CoreValue;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

public class ToStringTest {

    private static final String prefix = "prefix";
    private int count;

    @BeforeEach
    public void before() {
        count = 0;
    }

    @Test
    public void validateToStringImplementation() {
        final Expression e = not(and(eq(v(), v()), or(eqNum(v()), and(eqStr(v()), or(gtEqNum(v()), or(gtNum(v()), or(ltEqNum(v()), ltNum(v()))))))));
        final Token t = until("untilName", v(), v(), v(), post(repn(sub(opt(pre(rep(cho(token("refName"), any(n()), seq(nod(10), tie(def(n(), last(v())), v()), whl(def(n(), con(1), e), e), tie(t(), con(1))))), e)), v()), last(v())), e));
        final String output = t.toString();
        for (int i = 0; i < count; i++) {
            assertTrue(output.contains(prefix + i));
        }
        assertTrue(output.contains("refName"));
    }

    private String n() {
        return prefix + count++;
    }

    private Token t() {
        return any("a");
    }

    private ValueExpression v() {
        return fold(foldLeft(foldRight(rev(bytes(neg(add(div(mod(mul(sub(cat(last(scope(ref(n()), con(0)))), first(nth(exp(ref(n()), con(NOT_A_VALUE)), con(1)))), sub(CURRENT_ITERATION, con(1))), cat(ref(n()), ref(t()))), add(SELF, add(offset(ref(n())), add(CURRENT_OFFSET, count(ref(n())))))), elvis(ref(n()), ref(n())))))), Shorthand::add, last(ref(n()))), Shorthand::add), Shorthand::add, last(ref(n())));
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
        assertTrue(s1s.contains("_name_a_"));
        assertTrue(s1s.contains("_name_b_"));
        assertTrue(s1s.contains("_name_c_"));
    }

    @Test
    public void specialExpressions() {
        assertTrue(v().toString().contains("Self"));
        assertEquals("Self", SELF.toString());
        assertTrue(v().toString().contains("CurrentOffset"));
        assertEquals("CurrentOffset", CURRENT_OFFSET.toString());
        assertTrue(v().toString().contains("CurrentIteration"));
        assertEquals("CurrentIteration", CURRENT_ITERATION.toString());
        assertTrue(v().toString().contains("NOT_A_VALUE"));
        assertEquals("NOT_A_VALUE", NOT_A_VALUE.toString());
    }

    @Test
    public void encoding() {
        assertEquals("Encoding(UNSIGNED,US-ASCII,BIG_ENDIAN)", enc().toString());
        assertEquals("Encoding(SIGNED,US-ASCII,BIG_ENDIAN)", new Encoding(Sign.SIGNED).toString());
        assertEquals("Encoding(UNSIGNED,UTF-8,BIG_ENDIAN)", new Encoding(StandardCharsets.UTF_8).toString());
        assertEquals("Encoding(UNSIGNED,US-ASCII,LITTLE_ENDIAN)", new Encoding(ByteOrder.LITTLE_ENDIAN).toString());
    }

    @Test
    public void data() {
        final ParseState parseState = stream(1, 2);
        final String parseStateString = "ParseState(source:ByteStreamSource(InMemoryByteStream(2));offset:0;order:pg(EMPTY);scopeDepth:0;cache:size=0)";
        assertEquals(parseStateString, parseState.toString());

        final ParseState parseStateWithIterations = parseState.addBranch(rep(def("a",1))).iterate();
        final String parseStateWithIterationsString = "ParseState(source:ByteStreamSource(InMemoryByteStream(2));offset:0;order:pg(pg(terminator:Rep),pg(EMPTY),true);iterations:>Rep(Def(a,Const(0x01)))->1;scopeDepth:1;cache:size=0)";
        assertEquals(parseStateWithIterationsString, parseStateWithIterations.toString());

        final ParseState parseStateWithoutCache = parseStateWithIterations.withOrder(parseStateWithIterations.order);
        final String parseStateWithoutCacheString = "ParseState(source:ByteStreamSource(InMemoryByteStream(2));offset:0;order:pg(pg(terminator:Rep),pg(EMPTY),true);iterations:>Rep(Def(a,Const(0x01)))->1;scopeDepth:1;no-cache)";
        assertEquals(parseStateWithoutCacheString, parseStateWithoutCache.toString());

        final Optional<ParseState> result = Optional.of(parseState);
        assertEquals("Optional[" + parseState + "]", result.toString());

        final ParseValue pv1 = new ParseValue("name", NONE, createFromBytes(new byte[]{1, 2}), enc());
        final String pv1String = "pval(name:0x0102)";
        final Optional<Value> ov1 = Optional.of(pv1);
        final Optional<Value> ov2 = Optional.of(new CoreValue(createFromBytes(new byte[]{3}), enc()));
        assertEquals(">Optional[0x03]>Optional[" + pv1String + "]", ImmutableList.create(ov1).addHead(ov2).toString());

        final ParseValue pv2 = new ParseValue("two", NONE, createFromBytes(new byte[]{3, 4}), enc());
        final String pv2String = "pval(two:0x0304)";
        assertEquals(">" + pv2String + ">" + pv1String, ImmutableList.create(pv1).addHead(pv2).toString());
        assertEquals(">" + pv2String + ">" + pv1String, ImmutableList.create(pv1).addHead(pv2).toString());
    }

    @Test
    public void callback() {
        assertEquals("", Callbacks.NONE.toString());
        final String genericPrefix = "generic: ";
        final String tokenPrefix = "token: ";
        final String genericCallbackName = "genericName";
        final Callbacks singleCallbacks = Callbacks.create().add(makeToken("a"), makeCallback("first")).add(makeCallback(genericCallbackName));
        final String tokenCallback1Name = ">a->first";
        final String oneName = genericPrefix + genericCallbackName + "; " + tokenPrefix + tokenCallback1Name;
        assertEquals(oneName, singleCallbacks.toString());
        final Callbacks doubleCallbacks = Callbacks.create().add(makeToken("a"), makeCallback("first")).add(makeToken("b"), makeCallback("second"));
        final String tokenCallback2Name = ">b->second";
        final String twoName = tokenPrefix + tokenCallback2Name + tokenCallback1Name;
        assertEquals(twoName, doubleCallbacks.toString());
    }

    @Test
    public void toStringOnParseStateCollections() {
        final ParseState parseState = stream(1, 2);
        assertFalse(parseState.toString().contains(";iterations:"));
        assertFalse(parseState.toString().contains(";references:"));
        final ImmutableList<ImmutablePair<Token, BigInteger>> iterationsList = ImmutableList.create(new ImmutablePair<>(t(), BigInteger.ZERO));
        final ParseState parseStateWithIteration = new ParseState(parseState.order, parseState.cache, parseState.source, parseState.offset, iterationsList, new ImmutableList<>(), 0);
        assertTrue(parseStateWithIteration.toString().contains(";iterations:" + iterationsList.toString()));
        final ImmutableList<ParseReference> referencesList = ImmutableList.create(new ParseReference(BigInteger.ZERO, parseState.source, t()));
        final ParseState parseStateWithReference = new ParseState(parseState.order, parseState.cache, parseState.source, parseState.offset, new ImmutableList<>(), referencesList, 0);
        assertTrue(parseStateWithReference.toString().contains(";references:" + referencesList.toString()));
    }

    private Token makeToken(final String name) {
        return new Token(name, enc()) {
            @Override protected Optional<ParseState> parseImpl(final Environment environment) { return Optional.empty(); }
            @Override public String toString() { return name; }
        };
    }

    private Callback makeCallback(final String name) {
        return new Callback() {
            @Override public void handleSuccess(final Token token, final ParseState before, final ParseState after) { /* empty */ }
            @Override public void handleFailure(Token token, ParseState before) { /* empty */ }
            @Override public String toString() { return name; }
        };
    }

}
