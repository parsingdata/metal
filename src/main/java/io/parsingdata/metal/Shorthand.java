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

import io.parsingdata.metal.expression.value.reference.Len;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.True;
import io.parsingdata.metal.expression.comparison.ComparisonExpression;
import io.parsingdata.metal.expression.comparison.Eq;
import io.parsingdata.metal.expression.comparison.EqNum;
import io.parsingdata.metal.expression.comparison.EqStr;
import io.parsingdata.metal.expression.comparison.GtNum;
import io.parsingdata.metal.expression.comparison.LtNum;
import io.parsingdata.metal.expression.logical.And;
import io.parsingdata.metal.expression.logical.BinaryLogicalExpression;
import io.parsingdata.metal.expression.logical.Not;
import io.parsingdata.metal.expression.logical.Or;
import io.parsingdata.metal.expression.logical.UnaryLogicalExpression;
import io.parsingdata.metal.expression.value.BinaryValueExpression;
import io.parsingdata.metal.expression.value.Cat;
import io.parsingdata.metal.expression.value.Const;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.FoldLeft;
import io.parsingdata.metal.expression.value.FoldRight;
import io.parsingdata.metal.expression.value.Reducer;
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
import io.parsingdata.metal.expression.value.reference.CurrentOffset;
import io.parsingdata.metal.expression.value.reference.First;
import io.parsingdata.metal.expression.value.reference.Offset;
import io.parsingdata.metal.expression.value.reference.Ref;
import io.parsingdata.metal.expression.value.reference.Self;
import io.parsingdata.metal.token.Cho;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.Nod;
import io.parsingdata.metal.token.Opt;
import io.parsingdata.metal.token.Pre;
import io.parsingdata.metal.token.Rep;
import io.parsingdata.metal.token.RepN;
import io.parsingdata.metal.token.Seq;
import io.parsingdata.metal.token.Str;
import io.parsingdata.metal.token.StructSink;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.token.While;

public class Shorthand {

    public static Token def(final String name, final ValueExpression size, final Expression pred, final Encoding encoding) { return new Def(name, size, pred, encoding); }
    public static Token def(final String name, final ValueExpression size, final Expression pred) { return def(name, size, pred, null); }
    public static Token def(final String name, final ValueExpression size, final Encoding enc) { return def(name, size, null, enc); }
    public static Token def(final String name, final ValueExpression size) { return def(name, size, null, null); }
    public static Token def(final String name, final long size, final Expression pred, final Encoding encoding) { return def(name, con(size), pred, encoding); }
    public static Token def(final String name, final long size, final Expression pred) { return def(name, size, pred, null); }
    public static Token def(final String name, final long size, final Encoding enc) { return def(name, size, null, enc); }
    public static Token def(final String name, final long size) { return def(name, size, null, null); }
    public static Token cho(final Encoding e, final Token... tokens) { return new Cho(e, tokens); }
    public static Token cho(final Token... tokens) { return cho(null, tokens); }
    public static Token rep(final Token t, final Encoding e) { return new Rep(t, e); }
    public static Token rep(final Token t) { return new Rep(t, null); }
    public static Token repn(final Token t, final ValueExpression n, final Encoding e) { return new RepN(t, n, e); }
    public static Token repn(final Token t, final ValueExpression n) { return new RepN(t, n, null); }
    public static Token seq(final Encoding e, final Token... tokens) { return new Seq(e, tokens); }
    public static Token seq(final Token... tokens) { return seq(null, tokens); }
    public static Token str(final String n, final Token t, final Encoding e) { return str(n, t, e, null, null); }
    public static Token str(final String n, final Token t) { return str(n, t, null, null, null); }
    public static Token str(final String n, final Token t, final Encoding e, final StructSink s) { return new Str(n, t, e, s, null); }
    public static Token str(final String n, final Token t, final StructSink s) { return str(n, t, null, s, null); }
    public static Token str(final String n, final Token t, final Encoding e, final StructSink s, final Expression p) { return new Str(n, t, e, s, p); }
    public static Token str(final String n, final Token t, final StructSink s, final Expression p) { return str(n, t, null, s, p); }
    public static Token sub(final Token t, final ValueExpression a, final Encoding e) { return new io.parsingdata.metal.token.Sub(t, a, e); }
    public static Token sub(final Token t, final ValueExpression a) { return sub(t, a, null); }
    public static Token pre(final Token t, final Expression p, final Encoding e) { return new Pre(t, p, e); }
    public static Token pre(final Token t, final Expression p) { return pre(t, p, null); }
    public static Token whl(final Token t, final Expression p, final Encoding e) { return new While(t, p, e); }
    public static Token whl(final Token t, final Expression p) { return whl(t, p, null); }
    public static Token opt(final Token t, final Encoding e) { return new Opt(t, e); }
    public static Token opt(final Token t) { return opt(t, null); }
    public static Token nod(final ValueExpression s, final Encoding e) { return new Nod(s, e); }
    public static Token nod(final ValueExpression s) { return new Nod(s, null); }

    public static BinaryValueExpression add(final ValueExpression l, final ValueExpression r) { return new Add(l, r); }
    public static BinaryValueExpression div(final ValueExpression l, final ValueExpression r) { return new Div(l, r); }
    public static BinaryValueExpression mul(final ValueExpression l, final ValueExpression r) { return new Mul(l, r); }
    public static BinaryValueExpression sub(final ValueExpression l, final ValueExpression r) { return new Sub(l, r); }
    public static BinaryValueExpression mod(final ValueExpression l, final ValueExpression r) { return new Mod(l, r); }
    public static UnaryValueExpression neg(final ValueExpression v) { return new Neg(v); }
    public static BinaryValueExpression and(final ValueExpression l, final ValueExpression r) { return new io.parsingdata.metal.expression.value.bitwise.And(l, r); }
    public static BinaryValueExpression or(final ValueExpression l, final ValueExpression r) { return new io.parsingdata.metal.expression.value.bitwise.Or(l, r); }
    public static UnaryValueExpression not(final ValueExpression v) { return new io.parsingdata.metal.expression.value.bitwise.Not(v); }
    public static BinaryValueExpression shl(final ValueExpression l, final ValueExpression r) { return new ShiftLeft(l, r); }
    public static BinaryValueExpression shr(final ValueExpression l, final ValueExpression r) { return new ShiftRight(l, r); }
    public static ValueExpression con(final long v) { return con(v, new Encoding()); }
    public static ValueExpression con(final long v, final Encoding encoding) { return con(ConstantFactory.createFromNumeric(v, encoding)); }
    public static ValueExpression con(final String s) { return con(s, new Encoding()); }
    public static ValueExpression con(final String s, final Encoding encoding) { return con(ConstantFactory.createFromString(s, encoding)); }
    public static ValueExpression con(final Value v) { return new Const(v); }
    public static ValueExpression con(final Encoding enc, final int... values) { return new Const(new Value(toByteArray(values), enc)); }
    public static ValueExpression con(final int... values) { return con(new Encoding(), values); }
    public static final ValueExpression self = new Self();
    public static ValueExpression len(final String s) { return new Len(s); }
    public static ValueExpression ref(final String s) { return new Ref(s); }
    public static ValueExpression first(final String s) { return new First(s); }
    public static ValueExpression offset(final String s) { return new Offset(s); }
    public static final ValueExpression currentOffset = new CurrentOffset();
    public static ValueExpression cat(final ValueExpression l, final ValueExpression r) { return new Cat(l, r); }
    public static ValueExpression foldLeft(final String name, final Reducer reducer) { return new FoldLeft(name, reducer, null); }
    public static ValueExpression foldLeft(final String name, final Reducer reducer, final ValueExpression i) { return new FoldLeft(name, reducer, i); }
    public static ValueExpression foldRight(final String name, final Reducer reducer) { return new FoldRight(name, reducer, null); }
    public static ValueExpression foldRight(final String name, final Reducer reducer, final ValueExpression i) { return new FoldRight(name, reducer, i); }
    public static ValueExpression fold(final String name, final Reducer reducer) { return foldRight(name, reducer); }
    public static ValueExpression fold(final String name, final Reducer reducer, final ValueExpression i) { return foldRight(name, reducer, i); }

    public static BinaryLogicalExpression and(final Expression l, final Expression r) { return new And(l, r); }
    public static BinaryLogicalExpression or(final Expression l, final Expression r) { return new Or(l, r); }
    public static UnaryLogicalExpression not(final Expression e) { return new Not(e); }
    public static Expression expTrue() { return new True(); }

    public static ComparisonExpression eq(final ValueExpression p) { return new Eq(null, p); }
    public static ComparisonExpression eq(final ValueExpression c, final ValueExpression p) { return new Eq(c, p); }
    public static ComparisonExpression eqStr(final ValueExpression p) { return new EqStr(null, p); }
    public static ComparisonExpression eqStr(final ValueExpression c, final ValueExpression p) { return new EqStr(c, p); }
    public static ComparisonExpression eqNum(final ValueExpression p) { return new EqNum(null, p); }
    public static ComparisonExpression eqNum(final ValueExpression c, final ValueExpression p) { return new EqNum(c, p); }
    public static ComparisonExpression gtNum(final ValueExpression p) { return new GtNum(null, p); }
    public static ComparisonExpression gtNum(final ValueExpression c, final ValueExpression p) { return new GtNum(c, p); }
    public static ComparisonExpression ltNum(final ValueExpression p) { return new LtNum(null, p); }
    public static ComparisonExpression ltNum(final ValueExpression c, final ValueExpression p) { return new LtNum(c, p); }

    public static byte[] toByteArray(final int... bytes) {
        final byte[] out = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            out[i] = (byte) bytes[i];
        }
        return out;
    }

}
