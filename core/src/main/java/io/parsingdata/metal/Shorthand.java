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

import static io.parsingdata.metal.data.Slice.createFromBytes;
import static io.parsingdata.metal.encoding.Encoding.DEFAULT_ENCODING;
import static io.parsingdata.metal.token.Token.EMPTY_NAME;
import static io.parsingdata.metal.token.Token.NO_NAME;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.True;
import io.parsingdata.metal.expression.comparison.ComparisonExpression;
import io.parsingdata.metal.expression.comparison.Eq;
import io.parsingdata.metal.expression.comparison.EqNum;
import io.parsingdata.metal.expression.comparison.EqStr;
import io.parsingdata.metal.expression.comparison.GtEqNum;
import io.parsingdata.metal.expression.comparison.GtNum;
import io.parsingdata.metal.expression.comparison.LtEqNum;
import io.parsingdata.metal.expression.comparison.LtNum;
import io.parsingdata.metal.expression.logical.And;
import io.parsingdata.metal.expression.logical.BinaryLogicalExpression;
import io.parsingdata.metal.expression.logical.Not;
import io.parsingdata.metal.expression.logical.Or;
import io.parsingdata.metal.expression.logical.UnaryLogicalExpression;
import io.parsingdata.metal.expression.value.BinaryValueExpression;
import io.parsingdata.metal.expression.value.Bytes;
import io.parsingdata.metal.expression.value.Cat;
import io.parsingdata.metal.expression.value.Const;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.CoreValue;
import io.parsingdata.metal.expression.value.Elvis;
import io.parsingdata.metal.expression.value.Expand;
import io.parsingdata.metal.expression.value.FoldCat;
import io.parsingdata.metal.expression.value.FoldLeft;
import io.parsingdata.metal.expression.value.FoldRight;
import io.parsingdata.metal.expression.value.Join;
import io.parsingdata.metal.expression.value.Reverse;
import io.parsingdata.metal.expression.value.SingleValueExpression;
import io.parsingdata.metal.expression.value.UnaryValueExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.expression.value.arithmetic.Add;
import io.parsingdata.metal.expression.value.arithmetic.Div;
import io.parsingdata.metal.expression.value.arithmetic.Mod;
import io.parsingdata.metal.expression.value.arithmetic.Mul;
import io.parsingdata.metal.expression.value.arithmetic.Neg;
import io.parsingdata.metal.expression.value.arithmetic.Sub;
import io.parsingdata.metal.expression.value.bitwise.ShiftLeft;
import io.parsingdata.metal.expression.value.bitwise.ShiftRight;
import io.parsingdata.metal.expression.value.reference.Count;
import io.parsingdata.metal.expression.value.reference.CurrentIteration;
import io.parsingdata.metal.expression.value.reference.CurrentOffset;
import io.parsingdata.metal.expression.value.reference.First;
import io.parsingdata.metal.expression.value.reference.Last;
import io.parsingdata.metal.expression.value.reference.Len;
import io.parsingdata.metal.expression.value.reference.Nth;
import io.parsingdata.metal.expression.value.reference.Offset;
import io.parsingdata.metal.expression.value.reference.Ref;
import io.parsingdata.metal.expression.value.reference.Ref.DefinitionRef;
import io.parsingdata.metal.expression.value.reference.Ref.NameRef;
import io.parsingdata.metal.expression.value.reference.Self;
import io.parsingdata.metal.token.Cho;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.DefUntil;
import io.parsingdata.metal.token.Post;
import io.parsingdata.metal.token.Pre;
import io.parsingdata.metal.token.Rep;
import io.parsingdata.metal.token.RepN;
import io.parsingdata.metal.token.Seq;
import io.parsingdata.metal.token.Tie;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.token.TokenRef;
import io.parsingdata.metal.token.While;

public final class Shorthand {

    public static final Token EMPTY = def(EMPTY_NAME, 0L);
    public static final SingleValueExpression SELF = new Self();
    public static final SingleValueExpression CURRENT_OFFSET = new CurrentOffset();
    public static final SingleValueExpression CURRENT_ITERATION = new CurrentIteration(con(0));
    public static final Expression TRUE = new True();

    private Shorthand() {}

    /** "DEFinition": Instantiates a {@link Def}. */
    public static Token def(final String name, final SingleValueExpression size, final Encoding encoding) { return new Def(name, size, encoding); }

    /** "DEFinition": Instantiates a {@link Def} with {@code size = con(size)}. */
    public static Token def(final String name, final long size, final Encoding encoding) { return def(name, con(size), encoding); }

    /** "DEFinition": Instantiates a {@link Def} with {@code encoding = null}. */
    public static Token def(final String name, final SingleValueExpression size) { return def(name, size, (Encoding)null); }

    /** "DEFinition": Instantiates a {@link Def} with {@code size = con(size)} and {@code encoding = null}. */
    public static Token def(final String name, final long size) { return def(name, size, (Encoding)null); }

    /** "DEFinition": Instantiates a {@link Def} nested in a {@link Post}. */
    public static Token def(final String name, final SingleValueExpression size, final Expression predicate, final Encoding encoding) { return post(def(name, size, encoding), predicate); }

    /** "DEFinition": Instantiates a {@link Def} with {@code size = con(size)}, nested in a {@link Post}. */
    public static Token def(final String name, final long size, final Expression predicate, final Encoding encoding) { return def(name, con(size), predicate, encoding); }

    /** "DEFinition": Instantiates a {@link Def} with {@code encoding = null} nested in a {@link Post}. */
    public static Token def(final String name, final SingleValueExpression size, final Expression predicate) { return def(name, size, predicate, null); }

    /** "DEFinition": Instantiates a {@link Def} with {@code size = con(size)} and {@code encoding = null}, nested in a {@link Post}. */
    public static Token def(final String name, final long size, final Expression predicate) { return def(name, size, predicate, null); }


    /** "DEFinition": Instantiates a {@link DefUntil} where the size of the def is dynamically determined. */
    public static Token def(final String name, final ValueExpression initialSize, final ValueExpression stepSize, final ValueExpression maxSize, final Token terminator, final Encoding encoding) { return new DefUntil(name, initialSize, stepSize, maxSize, terminator, encoding); }

    /** "DEFinition": Instantiates a {@link DefUntil} where the size of the def is dynamically determined with {@code encoding = null}. */
    public static Token def(final String name, final ValueExpression initialSize, final ValueExpression stepSize, final ValueExpression maxSize, final Token terminator) { return def(name, initialSize, stepSize, maxSize, terminator, null); }

    /** "DEFinition": Instantiates a {@link DefUntil} where the size of the def is dynamically determined with {@code maxSize = null}. */
    public static Token def(final String name, final ValueExpression initialSize, final ValueExpression stepSize, final Token terminator, final Encoding encoding) { return def(name, initialSize, stepSize, null, terminator, encoding); }

    /** "DEFinition": Instantiates a {@link DefUntil} where the size of the def is dynamically determined with {@code maxSize = null} and {@code encoding = null}. */
    public static Token def(final String name, final ValueExpression initialSize, final ValueExpression stepSize, final Token terminator) { return def(name, initialSize, stepSize, null, terminator, null); }

    /** "DEFinition": Instantiates a {@link DefUntil} where the size of the def is dynamically determined with {@code stepSize = null} and {@code maxSize = null}. */
    public static Token def(final String name, final ValueExpression initialSize, final Token terminator, final Encoding encoding) { return def(name, initialSize, null, terminator, encoding); }

    /** "DEFinition": Instantiates a {@link DefUntil} where the size of the def is dynamically determined with {@code stepSize = null}, {@code maxSize = null} and {@code encoding = null}. */
    public static Token def(final String name, final ValueExpression initialSize, final Token terminator) { return def(name, initialSize, null, terminator, null); }

    /** "DEFinition": Instantiates a {@link DefUntil} where the size of the def is dynamically determined with {@code initialSize = null}, {@code stepSize = null} and {@code maxSize = null}. */
    public static Token def(final String name, final Token terminator, final Encoding encoding) { return def(name, null, terminator, encoding); }

    /** "DEFinition": Instantiates a {@link DefUntil} where the size of the def is dynamically determined with {@code initialSize = null}, {@code stepSize = null}, {@code maxSize = null} and {@code encoding = null}. */
    public static Token def(final String name, final Token terminator) { return def(name, terminator, null); }

    /** "DEFinition": Instantiates a {@link DefUntil} with the expression nested in a {@link Post} and {@code initialSize = con(1)}. */
    public static Token def(final String name, final Expression predicate, final Encoding encoding) { return def(name, con(1), post(EMPTY, predicate), encoding); }

    /** "DEFinition": Instantiates a {@link DefUntil} where the terminator is the expression nested in a {@link Post}, {@code initialSize = con(1)} and {@code encoding = null}. */
    public static Token def(final String name, final Expression predicate) { return def(name, predicate, null); }


    /** "NO Data": denotes data that is not required during parsing and afterwards. Instantiates a {@link Def} with {@code name = EMPTY_NAME} and {@code encoding = null}. */
    public static Token nod(final SingleValueExpression size) { return def(EMPTY_NAME, size); }

    /** "NO Data": denotes data that is not required during parsing and afterwards. Instantiates a {@link Def} with {@code size = con(size)}, {@code name = EMPTY_NAME} and {@code encoding = null}. */
    public static Token nod(final long size) { return nod(con(size)); }

    /** @see Cho */ public static Token cho(final String name, final Encoding encoding, final Token token1, final Token token2, final Token... tokens) { return new Cho(name, encoding, token1, token2, tokens); }
    /** @see Cho */ public static Token cho(final String name, final Token token1, final Token token2, final Token... tokens) { return cho(name, null, token1, token2, tokens); }
    /** @see Cho */ public static Token cho(final Encoding encoding, final Token token1, final Token token2, final Token... tokens) { return cho(NO_NAME, encoding, token1, token2, tokens); }
    /** @see Cho */ public static Token cho(final Token token1, final Token token2, final Token... tokens) { return cho((Encoding)null, token1, token2, tokens); }
    /** @see Rep */ public static Token rep(final String name, final Token token, final Encoding encoding) { return new Rep(name, token, encoding); }
    /** @see Rep */ public static Token rep(final String name, final Token token) { return rep(name, token, null); }
    /** @see Rep */ public static Token rep(final Token token, final Encoding encoding) { return rep(NO_NAME, token, encoding); }
    /** @see Rep */ public static Token rep(final Token token) { return rep(token, null); }
    /** @see RepN */ public static Token repn(final String name, final Token token, final SingleValueExpression n, final Encoding encoding) { return new RepN(name, token, n, encoding); }
    /** @see RepN */ public static Token repn(final String name, final Token token, final SingleValueExpression n) { return repn(name, token, n, null); }
    /** @see RepN */ public static Token repn(final Token token, final SingleValueExpression n, final Encoding encoding) { return repn(NO_NAME, token, n, encoding); }
    /** @see RepN */ public static Token repn(final Token token, final SingleValueExpression n) { return repn(token, n, null); }
    /** @see Seq */ public static Token seq(final String name, final Encoding encoding, final Token token1, final Token token2, final Token... tokens) { return new Seq(name, encoding, token1, token2, tokens); }
    /** @see Seq */ public static Token seq(final String name, final Token token1, final Token token2, final Token... tokens) { return seq(name, null, token1, token2, tokens); }
    /** @see Seq */ public static Token seq(final Encoding encoding, final Token token1, final Token token2, final Token... tokens) { return seq(NO_NAME, encoding, token1, token2, tokens); }
    /** @see Seq */ public static Token seq(final Token token1, final Token token2, final Token... tokens) { return seq((Encoding)null, token1, token2, tokens); }
    /** @see io.parsingdata.metal.token.Sub */ public static Token sub(final String name, final Token token, final ValueExpression address, final Encoding encoding) { return new io.parsingdata.metal.token.Sub(name, token, address, encoding); }
    /** @see io.parsingdata.metal.token.Sub */ public static Token sub(final String name, final Token token, final ValueExpression address) { return sub(name, token, address, null); }
    /** @see io.parsingdata.metal.token.Sub */ public static Token sub(final Token token, final ValueExpression address, final Encoding encoding) { return sub(NO_NAME, token, address, encoding); }
    /** @see io.parsingdata.metal.token.Sub */ public static Token sub(final Token token, final ValueExpression address) { return sub(token, address, null); }
    /** @see Pre */ public static Token pre(final String name, final Token token, final Expression predicate, final Encoding encoding) { return new Pre(name, token, predicate, encoding); }
    /** @see Pre */ public static Token pre(final String name, final Token token, final Expression predicate) { return pre(name, token, predicate, null); }
    /** @see Pre */ public static Token pre(final Token token, final Expression predicate, final Encoding encoding) { return pre(NO_NAME, token, predicate, encoding); }
    /** @see Pre */ public static Token pre(final Token token, final Expression predicate) { return pre(token, predicate, null); }
    /** @see Post */ public static Token post(final String name, final Token token, final Expression predicate, final Encoding encoding) { return new Post(name, token, predicate, encoding); }
    /** @see Post */ public static Token post(final String name, final Token token, final Expression predicate) { return post(name, token, predicate, null); }
    /** @see Post */ public static Token post(final Token token, final Expression predicate, final Encoding encoding) { return post(NO_NAME, token, predicate, encoding); }
    /** @see Post */ public static Token post(final Token token, final Expression predicate) { return post(token, predicate, null); }
    /** @see While */ public static Token whl(final String name, final Token token, final Expression predicate, final Encoding encoding) { return new While(name, token, predicate, encoding); }
    /** @see While */ public static Token whl(final String name, final Token token, final Expression predicate) { return whl(name, token, predicate, null); }
    /** @see While */ public static Token whl(final Token token, final Expression predicate, final Encoding encoding) { return whl(NO_NAME, token, predicate, encoding); }
    /** @see While */ public static Token whl(final Token token, final Expression predicate) { return whl(NO_NAME, token, predicate); }

    /** "OPTional": denotes an optional token that succeeds regardless of whether its nested token successfully parses. Instantiates a {@link Cho} with {@code token1 = token} and {@code token2 = EMPTY}. */
    public static Token opt(final String name, final Token token, final Encoding encoding) { return cho(name, encoding, token, EMPTY); }

    /** "OPTional": denotes an optional token that succeeds regardless of whether its nested token successfully parses. Instantiates a {@link Cho} with {@code name = NO_NAME}, {@code token1 = token} and {@code token2 = EMPTY}. */
    public static Token opt(final Token token, final Encoding encoding) { return opt(NO_NAME, token, encoding); }

    /** "OPTional": denotes an optional token that succeeds regardless of whether its nested token successfully parses. Instantiates a {@link Cho} with {@code encoding = null}, {@code token1 = token} and {@code token2 = EMPTY}. */
    public static Token opt(final String name, final Token token) { return opt(name, token, null); }

    /** "OPTional": denotes an optional token that succeeds regardless of whether its nested token successfully parses. Instantiates a {@link Cho} with {@code name = NO_NAME}, {@code encoding = null}, {@code token1 = token} and {@code token2 = EMPTY}. */
    public static Token opt(final Token token) { return opt(token, null); }

    /** @see TokenRef */ public static Token token(final String tokenName) { return new TokenRef(NO_NAME, tokenName, null); }
    /** @see Tie */ public static Token tie(final String name, final Token token, final ValueExpression dataExpression, final Encoding encoding) { return new Tie(name, token, dataExpression, encoding); }
    /** @see Tie */ public static Token tie(final String name, final Token token, final ValueExpression dataExpression) { return tie(name, token, dataExpression, null); }
    /** @see Tie */ public static Token tie(final Token token, final ValueExpression dataExpression, final Encoding encoding) { return tie(NO_NAME, token, dataExpression, encoding); }
    /** @see Tie */ public static Token tie(final Token token, final ValueExpression dataExpression) { return tie(token, dataExpression, null); }


    /** "DEFinition": Instantiates a {@link DefUntil} and its terminator nested in a {@link Seq}. */
    public static Token until(final String name, final ValueExpression initialSize, final ValueExpression stepSize, final ValueExpression maxSize, final Token terminator, final Encoding encoding) { return seq(def(name, initialSize, stepSize, maxSize, terminator, encoding), terminator); }

    /** "DEFinition": Instantiates a {@link DefUntil} and its terminator nested in a {@link Seq} with {@code encoding = null}. */
    public static Token until(final String name, final ValueExpression initialSize, final ValueExpression stepSize, final ValueExpression maxSize, final Token terminator) { return until(name, initialSize, stepSize, maxSize, terminator, null); }

    /** "DEFinition": Instantiates a {@link DefUntil} and its terminator nested in a {@link Seq} with {@code maxSize = null}. */
    public static Token until(final String name, final ValueExpression initialSize, final ValueExpression stepSize, final Token terminator, final Encoding encoding) { return until(name, initialSize, stepSize, null, terminator, encoding); }

    /** "DEFinition": Instantiates a {@link DefUntil} and its terminator nested in a {@link Seq} with {@code maxSize = null} and {@code encoding = null}. */
    public static Token until(final String name, final ValueExpression initialSize, final ValueExpression stepSize, final Token terminator) { return until(name, initialSize, stepSize, null, terminator, null); }

    /** "DEFinition": Instantiates a {@link DefUntil} and its terminator nested in a {@link Seq} with {@code stepSize = null} and {@code maxSize = null}. */
    public static Token until(final String name, final ValueExpression initialSize, final Token terminator, final Encoding encoding) { return until(name, initialSize, null, terminator, encoding); }

    /** "DEFinition": Instantiates a {@link DefUntil} and its terminator nested in a {@link Seq} with {@code stepSize = null}, {@code maxSize = null} and {@code encoding = null}. */
    public static Token until(final String name, final ValueExpression initialSize, final Token terminator) { return until(name, initialSize, null, terminator, null); }

    /** "DEFinition": Instantiates a {@link DefUntil} and its terminator nested in a {@link Seq} with {@code initialSize = null}, {@code stepSize = null} and {@code maxSize = null}. */
    public static Token until(final String name, final Token terminator, final Encoding encoding) { return until(name, null, terminator, encoding); }

    /** "DEFinition": Instantiates a {@link DefUntil} and its terminator nested in a {@link Seq} with {@code initialSize = null}, {@code stepSize = null}, {@code maxSize = null} and {@code encoding = null}. */
    public static Token until(final String name, final Token terminator) { return until(name, terminator, null); }


    /** "WHEN": denotes a logical implication, parses the {@code token} only if the {@code predicate} evaluates to {code true} and subsequently only fails if {@code token} does not successfully parse. A composition of {@link Cho} and {@link Pre}. */
    public static Token when(final String name, final Token token, final Expression predicate, final Encoding encoding) { return cho(name, encoding, pre(def(EMPTY_NAME, 0), not(predicate)), token); }

    /** "WHEN": denotes a logical implication, parses the {@code token} only if the {@code predicate} evaluates to {code true} and subsequently only fails if {@code token} does not successfully parse. A composition of {@link Cho} and {@link Pre} with {@code name = EMPTY_NAME}. */
    public static Token when(final Token token, final Expression predicate, final Encoding encoding) { return when(EMPTY_NAME, token, predicate, encoding); }

    /** "WHEN": denotes a logical implication, parses the {@code token} only if the {@code predicate} evaluates to {code true} and subsequently only fails if {@code token} does not successfully parse. A composition of {@link Cho} and {@link Pre} with {@code encoding = null}. */
    public static Token when(final String name, final Token token, final Expression predicate) { return when(name, token, predicate, null); }

    /** "WHEN": denotes a logical implication, parses the {@code token} only if the {@code predicate} evaluates to {code true} and subsequently only fails if {@code token} does not successfully parse. A composition of {@link Cho} and {@link Pre} with {@code name = EMPTY_NAME} and {@code encoding = null}. */
    public static Token when(final Token token, final Expression predicate) { return when(token, predicate, null); }

    /** @see Add */ public static BinaryValueExpression add(final ValueExpression left, final ValueExpression right) { return new Add(left, right); }
    /** @see Add */ public static SingleValueExpression add(final SingleValueExpression left, final SingleValueExpression right) { return last(new Add(left, right)); }
    /** @see Div */ public static BinaryValueExpression div(final ValueExpression left, final ValueExpression right) { return new Div(left, right); }
    /** @see Div */ public static SingleValueExpression div(final SingleValueExpression left, final SingleValueExpression right) { return last(new Div(left, right)); }
    /** @see Mul */ public static BinaryValueExpression mul(final ValueExpression left, final ValueExpression right) { return new Mul(left, right); }
    /** @see Mul */ public static SingleValueExpression mul(final SingleValueExpression left, final SingleValueExpression right) { return last(new Mul(left, right)); }
    /** @see Sub */ public static BinaryValueExpression sub(final ValueExpression left, final ValueExpression right) { return new Sub(left, right); }
    /** @see Sub */ public static SingleValueExpression sub(final SingleValueExpression left, final SingleValueExpression right) { return last(new Sub(left, right)); }
    /** @see Mod */ public static BinaryValueExpression mod(final ValueExpression left, final ValueExpression right) { return new Mod(left, right); }
    /** @see Mod */ public static SingleValueExpression mod(final SingleValueExpression left, final SingleValueExpression right) { return last(new Mod(left, right)); }
    /** @see Neg */ public static UnaryValueExpression neg(final ValueExpression operand) { return new Neg(operand); }
    /** @see Neg */ public static SingleValueExpression neg(final SingleValueExpression operand) { return last(new Neg(operand)); }
    /** @see io.parsingdata.metal.expression.value.bitwise.And */ public static BinaryValueExpression and(final ValueExpression left, final ValueExpression right) { return new io.parsingdata.metal.expression.value.bitwise.And(left, right); }
    /** @see io.parsingdata.metal.expression.value.bitwise.And */ public static SingleValueExpression and(final SingleValueExpression left, final SingleValueExpression right) { return last(new io.parsingdata.metal.expression.value.bitwise.And(left, right)); }
    /** @see io.parsingdata.metal.expression.value.bitwise.Or */ public static BinaryValueExpression or(final ValueExpression left, final ValueExpression right) { return new io.parsingdata.metal.expression.value.bitwise.Or(left, right); }
    /** @see io.parsingdata.metal.expression.value.bitwise.Or */ public static SingleValueExpression or(final SingleValueExpression left, final SingleValueExpression right) { return last(new io.parsingdata.metal.expression.value.bitwise.Or(left, right)); }
    /** @see io.parsingdata.metal.expression.value.bitwise.Not */ public static UnaryValueExpression not(final ValueExpression operand) { return new io.parsingdata.metal.expression.value.bitwise.Not(operand); }
    /** @see io.parsingdata.metal.expression.value.bitwise.Not */ public static SingleValueExpression not(final SingleValueExpression operand) { return last(new io.parsingdata.metal.expression.value.bitwise.Not(operand)); }
    /** @see ShiftLeft */ public static BinaryValueExpression shl(final ValueExpression left, final ValueExpression right) { return new ShiftLeft(left, right); }
    /** @see ShiftLeft */ public static SingleValueExpression shl(final SingleValueExpression left, final SingleValueExpression right) { return last(new ShiftLeft(left, right)); }
    /** @see ShiftRight */ public static BinaryValueExpression shr(final ValueExpression left, final ValueExpression right) { return new ShiftRight(left, right); }
    /** @see ShiftRight */ public static SingleValueExpression shr(final SingleValueExpression left, final SingleValueExpression right) { return last(new ShiftRight(left, right)); }

    /** "CONstant": instantiates {@link Const} with {@code value}. */
    public static SingleValueExpression con(final Value value) { return new Const(value); }

    /** "CONstant": instantiates {@link Const} with a {@link Value} object using the provided {@code encoding} and {@code values} as a byte array denoting a single value. */
    public static SingleValueExpression con(final Encoding encoding, final int... values) { return new Const(new CoreValue(createFromBytes(toByteArray(values)), encoding)); }

    /** "CONstant": instantiates {@link Const} with a {@link Value} object using {@code DEFAULT_ENCODING} and {@code values} as a byte array denoting a single value. */
    public static SingleValueExpression con(final int... values) { return con(DEFAULT_ENCODING, values); }

    /** "CONstant": instantiates {@link Const} with a {@link Value} object using {@code DEFAULT_ENCODING} and {@code value} denoting a single value. */
    public static SingleValueExpression con(final byte[] value) { return con(value, DEFAULT_ENCODING); }

    /** "CONstant": instantiates {@link Const} with a {@link Value} object using {@code DEFAULT_ENCODING} and {@code value} denoting a single value. */
    public static SingleValueExpression con(final byte[] value, final Encoding encoding) { return con(ConstantFactory.createFromBytes(value, encoding)); }

    /** "CONstant": instantiates {@link Const} with a {@link Value} object using {@code value} and the provided {@code encoding}. */
    public static SingleValueExpression con(final long value, final Encoding encoding) { return con(ConstantFactory.createFromNumeric(value, encoding)); }

    /** "CONstant": instantiates {@link Const} with a {@link Value} object using {@code value} and {@code DEFAULT_ENCODING}. */
    public static SingleValueExpression con(final long value) { return con(value, DEFAULT_ENCODING); }

    /** "CONstant": instantiates {@link Const} with a {@link Value} object using {@code value} and the provided {@code encoding}. */
    public static SingleValueExpression con(final String value, final Encoding encoding) { return con(ConstantFactory.createFromString(value, encoding)); }

    /** "CONstant": instantiates {@link Const} with a {@link Value} object using {@code value} and {@code DEFAULT_ENCODING}. */
    public static SingleValueExpression con(final String value) { return con(value, DEFAULT_ENCODING); }

    /** @see Len */ public static ValueExpression len(final ValueExpression operand) { return new Len(operand); }
    /** @see Len */ public static SingleValueExpression len(final SingleValueExpression operand) { return last(new Len(operand)); }

    /** @deprecated Use {@link #ref(SingleValueExpression, String, String...)} or {@link #last(NameRef)} in combination with {@link #ref(String, String...)}  if limit is 1, instead. */
    public static NameRef ref(final String name, final SingleValueExpression limit) { return new NameRef(limit, name); }
    /** @see Ref */ public static NameRef ref(final String name, final String... names) { return new NameRef(name, names); }
    /** @see Ref */ public static NameRef ref(final SingleValueExpression limit, final String name, final String... names) { return new NameRef(limit, name, names); }

    /** @deprecated Use {@link #ref(SingleValueExpression, Token, Token...)} or {@link #last(DefinitionRef)} in combination with {@link #ref(Token, Token...)} if limit is 1, instead. */
    public static DefinitionRef ref(final Token definition, final SingleValueExpression limit) { return new DefinitionRef(limit, definition); }
    /** @see Ref */ public static DefinitionRef ref(final Token definition, final Token... definitions) { return new DefinitionRef(definition, definitions); }
    /** @see Ref */ public static DefinitionRef ref(final SingleValueExpression limit, final Token definition, final Token... definitions) { return new DefinitionRef(limit, definition, definitions); }

    /** @see First */ public static SingleValueExpression first(final ValueExpression operand) { return new First(operand); }
    /** @see Last */ public static SingleValueExpression last(final ValueExpression operand) { return new Last(operand); }
    /** @see Last */ public static SingleValueExpression last(final NameRef operand) { return new Last(operand.withLimit(con(1))); }
    /** @see Last */ public static SingleValueExpression last(final DefinitionRef operand) { return new Last(operand.withLimit(con(1))); }
    /** @see Nth */ public static ValueExpression nth(final ValueExpression values, final ValueExpression indices) { return new Nth(values, indices); }
    /** @see Nth */ public static SingleValueExpression nth(final ValueExpression values, final SingleValueExpression index) { return last(new Nth(values, index)); }
    /** @see Offset */ public static ValueExpression offset(final ValueExpression operand) { return new Offset(operand); }
    /** @see Offset */ public static SingleValueExpression offset(final SingleValueExpression operand) { return last(new Offset(operand)); }
    /** @see CurrentIteration */ public static SingleValueExpression iteration(final int level) { return iteration(con(level)); }
    /** @see CurrentIteration */ public static SingleValueExpression iteration(final SingleValueExpression level) { return new CurrentIteration(level); }
    /** @see Cat */ public static ValueExpression cat(final ValueExpression left, final ValueExpression right) { return new Cat(left, right); }
    /** @see Cat */ public static SingleValueExpression cat(final SingleValueExpression left, final SingleValueExpression right) { return last(new Cat(left, right)); }
    /** @see FoldCat */ public static SingleValueExpression cat(final ValueExpression operand) { return new FoldCat(operand); }
    /** @see Elvis */ public static ValueExpression elvis(final ValueExpression left, final ValueExpression right) { return new Elvis(left, right); }
    /** @see Elvis */ public static SingleValueExpression elvis(final SingleValueExpression left, final SingleValueExpression right) { return last(new Elvis(left, right)); }
    /** @see Count */ public static SingleValueExpression count(final ValueExpression operand) { return new Count(operand); }
    /** @see FoldLeft */ public static SingleValueExpression foldLeft(final ValueExpression values, final BinaryOperator<SingleValueExpression> reducer) { return new FoldLeft(values, reducer, null); }
    /** @see FoldLeft */ public static SingleValueExpression foldLeft(final ValueExpression values, final BinaryOperator<SingleValueExpression> reducer, final SingleValueExpression initial) { return new FoldLeft(values, reducer, initial); }
    /** @see FoldRight */ public static SingleValueExpression foldRight(final ValueExpression values, final BinaryOperator<SingleValueExpression> reducer) { return new FoldRight(values, reducer, null); }
    /** @see FoldRight */ public static SingleValueExpression foldRight(final ValueExpression values, final BinaryOperator<SingleValueExpression> reducer, final SingleValueExpression initial) { return new FoldRight(values, reducer, initial); }
    /** @see FoldRight */public static SingleValueExpression fold(final ValueExpression values, final BinaryOperator<SingleValueExpression> reducer) { return foldRight(values, reducer); }
    /** @see FoldRight */public static SingleValueExpression fold(final ValueExpression values, final BinaryOperator<SingleValueExpression> reducer, final SingleValueExpression initial) { return foldRight(values, reducer, initial); }
    /** @see Reverse */ public static ValueExpression rev(final ValueExpression values) { return new Reverse(values); }
    /** @see Expand */ public static ValueExpression exp(final ValueExpression base, final SingleValueExpression count) { return new Expand(base, count); }
    /** @see Join */ public static ValueExpression join(final ValueExpression... expressions) { return new Join(expressions); }

    /** "MAPLEFT": denotes a map operation using the provided {@code func}, applied once for each result of evaluating {@code left} and reusing the single result of evaluating {@code rightExpand}. */
    public static BinaryValueExpression mapLeft(final BiFunction<ValueExpression, ValueExpression, BinaryValueExpression> func, final ValueExpression left, final SingleValueExpression rightExpand) { return func.apply(left, exp(rightExpand, count(left))); }

    /** "MAPRIGHT": denotes a map operation using the provided {@code func}, applied once for each result of evaluating {@code right} and reusing the single result of evaluating {@code leftExpand}. */
    public static BinaryValueExpression mapRight(final BiFunction<ValueExpression, ValueExpression, BinaryValueExpression> func, final SingleValueExpression leftExpand, final ValueExpression right) { return func.apply(exp(leftExpand, count(right)), right); }

    /** @see Bytes */ public static ValueExpression bytes(final ValueExpression operand) { return new Bytes(operand); }

    /** @see Ref */ public static NameRef scope(final NameRef operand) { return scope(operand, con(0)); }
    /** @see Ref */ public static NameRef scope(final NameRef operand, final SingleValueExpression scopeSize) { return operand.withScope(scopeSize); }
    /** @see Ref */ public static DefinitionRef scope(final DefinitionRef operand) { return scope(operand, con(0)); }
    /** @see Ref */ public static DefinitionRef scope(final DefinitionRef operand, final SingleValueExpression scopeSize) { return operand.withScope(scopeSize); }

    /** @see And */ public static BinaryLogicalExpression and(final Expression left, final Expression right) { return new And(left, right); }
    /** @see Or */ public static BinaryLogicalExpression or(final Expression left, final Expression right) { return new Or(left, right); }
    /** @see Not */ public static UnaryLogicalExpression not(final Expression operand) { return new Not(operand); }

    /** @see Eq */ public static ComparisonExpression eq(final ValueExpression predicate) { return new Eq(null, predicate); }
    /** @see Eq */ public static ComparisonExpression eq(final ValueExpression value, final ValueExpression predicate) { return new Eq(value, predicate); }
    /** @see EqStr */ public static ComparisonExpression eqStr(final ValueExpression predicate) { return new EqStr(null, predicate); }
    /** @see EqStr */ public static ComparisonExpression eqStr(final ValueExpression value, final ValueExpression predicate) { return new EqStr(value, predicate); }
    /** @see EqNum */ public static ComparisonExpression eqNum(final ValueExpression predicate) { return new EqNum(null, predicate); }
    /** @see EqNum */ public static ComparisonExpression eqNum(final ValueExpression value, final ValueExpression predicate) { return new EqNum(value, predicate); }
    /** @see GtEqNum */ public static ComparisonExpression gtEqNum(final ValueExpression predicate) { return new GtEqNum(null, predicate); }
    /** @see GtEqNum */ public static ComparisonExpression gtEqNum(final ValueExpression value, final ValueExpression predicate) { return new GtEqNum(value, predicate); }
    /** @see GtNum */ public static ComparisonExpression gtNum(final ValueExpression predicate) { return new GtNum(null, predicate); }
    /** @see GtNum */ public static ComparisonExpression gtNum(final ValueExpression value, final ValueExpression predicate) { return new GtNum(value, predicate); }
    /** @see LtEqNum */ public static ComparisonExpression ltEqNum(final ValueExpression predicate) { return new LtEqNum(null, predicate); }
    /** @see LtEqNum */ public static ComparisonExpression ltEqNum(final ValueExpression value, final ValueExpression predicate) { return new LtEqNum(value, predicate); }
    /** @see LtNum */ public static ComparisonExpression ltNum(final ValueExpression predicate) { return new LtNum(null, predicate); }
    /** @see LtNum */ public static ComparisonExpression ltNum(final ValueExpression value, final ValueExpression predicate) { return new LtNum(value, predicate); }

    public static byte[] toByteArray(final int... bytes) {
        final byte[] outBytes = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            outBytes[i] = (byte) bytes[i];
        }
        return outBytes;
    }

}
