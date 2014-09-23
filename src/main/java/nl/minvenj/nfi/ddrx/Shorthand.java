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

package nl.minvenj.nfi.ddrx;

import java.util.Arrays;

import nl.minvenj.nfi.ddrx.encoding.Encoding;
import nl.minvenj.nfi.ddrx.expression.Expression;
import nl.minvenj.nfi.ddrx.expression.True;
import nl.minvenj.nfi.ddrx.expression.comparison.ComparisonExpression;
import nl.minvenj.nfi.ddrx.expression.comparison.Eq;
import nl.minvenj.nfi.ddrx.expression.comparison.EqNum;
import nl.minvenj.nfi.ddrx.expression.comparison.EqStr;
import nl.minvenj.nfi.ddrx.expression.comparison.GtNum;
import nl.minvenj.nfi.ddrx.expression.comparison.LtNum;
import nl.minvenj.nfi.ddrx.expression.logical.And;
import nl.minvenj.nfi.ddrx.expression.logical.BinaryLogicalExpression;
import nl.minvenj.nfi.ddrx.expression.logical.Not;
import nl.minvenj.nfi.ddrx.expression.logical.Or;
import nl.minvenj.nfi.ddrx.expression.logical.UnaryLogicalExpression;
import nl.minvenj.nfi.ddrx.expression.value.BinaryValueExpression;
import nl.minvenj.nfi.ddrx.expression.value.Cat;
import nl.minvenj.nfi.ddrx.expression.value.Const;
import nl.minvenj.nfi.ddrx.expression.value.ConstantFactory;
import nl.minvenj.nfi.ddrx.expression.value.Reduce;
import nl.minvenj.nfi.ddrx.expression.value.Reducer;
import nl.minvenj.nfi.ddrx.expression.value.UnaryValueExpression;
import nl.minvenj.nfi.ddrx.expression.value.Value;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;
import nl.minvenj.nfi.ddrx.expression.value.arithmetic.Add;
import nl.minvenj.nfi.ddrx.expression.value.arithmetic.Div;
import nl.minvenj.nfi.ddrx.expression.value.arithmetic.Mod;
import nl.minvenj.nfi.ddrx.expression.value.arithmetic.Mul;
import nl.minvenj.nfi.ddrx.expression.value.arithmetic.Neg;
import nl.minvenj.nfi.ddrx.expression.value.arithmetic.Sub;
import nl.minvenj.nfi.ddrx.expression.value.reference.First;
import nl.minvenj.nfi.ddrx.expression.value.reference.Offset;
import nl.minvenj.nfi.ddrx.expression.value.reference.Ref;
import nl.minvenj.nfi.ddrx.expression.value.reference.Self;
import nl.minvenj.nfi.ddrx.token.Cho;
import nl.minvenj.nfi.ddrx.token.Def;
import nl.minvenj.nfi.ddrx.token.Rep;
import nl.minvenj.nfi.ddrx.token.Seq;
import nl.minvenj.nfi.ddrx.token.Str;
import nl.minvenj.nfi.ddrx.token.StructSink;
import nl.minvenj.nfi.ddrx.token.Token;

public class Shorthand {

    public static Token def(final String name, final ValueExpression size, final Expression pred, final Encoding encoding) { return new Def(name, size, pred, encoding); }
    public static Token def(final String name, final ValueExpression size, final Expression pred) { return new Def(name, size, pred); }
    public static Token def(final String name, final ValueExpression size) { return def(name, size, expTrue()); }
    public static Token cho(final Token l, final Token r, final Token... tokens) { return tokens.length == 0 ? new Cho(l, r) : cho(l, cho(r, tokens[0], Arrays.copyOfRange(tokens, 1, tokens.length))); }
    public static Token rep(final Token t) { return new Rep(t); }
    public static Token seq(final Token l, final Token r, final Token... tokens) { return tokens.length == 0 ? new Seq(l, r) : seq(l, seq(r, tokens[0], Arrays.copyOfRange(tokens, 1, tokens.length))); }
    public static Token str(final String n, final Token t) { return new Str(n, t); }
    public static Token str(final String n, final Token t, final Encoding e) { return new Str(n, t, e); }
    public static Token str(final String n, final Token t, final StructSink s) { return new Str(n, t, s); }
    public static Token str(final String n, final Token t, final Encoding e, final StructSink s) { return new Str(n, t, e, s); }

    public static BinaryValueExpression add(final ValueExpression l, final ValueExpression r) { return new Add(l, r); }
    public static BinaryValueExpression div(final ValueExpression l, final ValueExpression r) { return new Div(l, r); }
    public static BinaryValueExpression mul(final ValueExpression l, final ValueExpression r) { return new Mul(l, r); }
    public static BinaryValueExpression sub(final ValueExpression l, final ValueExpression r) { return new Sub(l, r); }
    public static BinaryValueExpression mod(final ValueExpression l, final ValueExpression r) { return new Mod(l, r); }
    public static UnaryValueExpression neg(final ValueExpression v) { return new Neg(v); }
    public static ValueExpression con(final long v) { return con(v, new Encoding()); }
    public static ValueExpression con(final long v, final Encoding encoding) { return new Const(ConstantFactory.createFromNumeric(v, encoding)); }
    public static ValueExpression con(final String s) { return con(s, new Encoding()); }
    public static ValueExpression con(final String s, final Encoding encoding) { return new Const(ConstantFactory.createFromString(s, encoding)); }
    public static ValueExpression con(final Value v) { return new Const(v); }
    public static final ValueExpression self = new Self();
    public static ValueExpression ref(final String s) { return new Ref(s); }
    public static ValueExpression first(final String s) { return new First(s); }
    public static ValueExpression offset(final String s) { return new Offset(s); }
    public static ValueExpression cat(final ValueExpression l, final ValueExpression r) { return new Cat(l, r); }
    public static ValueExpression reduce(final String name, final Reducer reducer) { return new Reduce(name, reducer); }

    public static BinaryLogicalExpression and(final Expression l, final Expression r) { return new And(l, r); }
    public static BinaryLogicalExpression or(final Expression l, final Expression r) { return new Or(l, r); }
    public static UnaryLogicalExpression not(final Expression e) { return new Not(e); }
    public static Expression expTrue() { return new True(); }

    public static ComparisonExpression eq(final ValueExpression p) { return new Eq(p); }
    public static ComparisonExpression eq(final ValueExpression c, ValueExpression p) { return new Eq(c, p); }
    public static ComparisonExpression eqStr(final ValueExpression p) { return new EqStr(p); }
    public static ComparisonExpression eqStr(final ValueExpression c, final ValueExpression p) { return new EqStr(c, p); }
    public static ComparisonExpression eqNum(final ValueExpression p) { return new EqNum(p); }
    public static ComparisonExpression eqNum(final ValueExpression c, final ValueExpression p) { return new EqNum(c, p); }
    public static ComparisonExpression gtNum(final ValueExpression p) { return new GtNum(p); }
    public static ComparisonExpression gtNum(final ValueExpression c, final ValueExpression p) { return new GtNum(c, p); }
    public static ComparisonExpression ltNum(final ValueExpression p) { return new LtNum(p); }
    public static ComparisonExpression ltNum(final ValueExpression c, final ValueExpression p) { return new LtNum(c, p); }

}
