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

import java.math.BigInteger;

import nl.minvenj.nfi.ddrx.expression.Expression;
import nl.minvenj.nfi.ddrx.expression.True;
import nl.minvenj.nfi.ddrx.expression.comparison.ComparisonExpression;
import nl.minvenj.nfi.ddrx.expression.comparison.Eq;
import nl.minvenj.nfi.ddrx.expression.comparison.Gt;
import nl.minvenj.nfi.ddrx.expression.comparison.Lt;
import nl.minvenj.nfi.ddrx.expression.logical.And;
import nl.minvenj.nfi.ddrx.expression.logical.BinaryLogicalExpression;
import nl.minvenj.nfi.ddrx.expression.logical.Not;
import nl.minvenj.nfi.ddrx.expression.logical.Or;
import nl.minvenj.nfi.ddrx.expression.logical.UnaryLogicalExpression;
import nl.minvenj.nfi.ddrx.expression.value.Add;
import nl.minvenj.nfi.ddrx.expression.value.BinaryValueExpression;
import nl.minvenj.nfi.ddrx.expression.value.Con;
import nl.minvenj.nfi.ddrx.expression.value.Div;
import nl.minvenj.nfi.ddrx.expression.value.Mul;
import nl.minvenj.nfi.ddrx.expression.value.Neg;
import nl.minvenj.nfi.ddrx.expression.value.Ref;
import nl.minvenj.nfi.ddrx.expression.value.Sub;
import nl.minvenj.nfi.ddrx.expression.value.UnaryValueExpression;
import nl.minvenj.nfi.ddrx.expression.value.Value;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;
import nl.minvenj.nfi.ddrx.token.Cho;
import nl.minvenj.nfi.ddrx.token.Rep;
import nl.minvenj.nfi.ddrx.token.Seq;
import nl.minvenj.nfi.ddrx.token.Token;
import nl.minvenj.nfi.ddrx.token.Val;

public class Shorthand {
    
    public static Token val(String name, ValueExpression size, Expression pred) { return new Val(name, size, pred); }
    public static Token cho(Token l, Token r) { return new Cho(l, r); }
    public static Token rep(Token t) { return new Rep(t); }
    public static Token seq(Token l, Token r) { return new Seq(l, r); }
    
    public static BinaryValueExpression add(ValueExpression l, ValueExpression r) { return new Add(l, r); }
    public static BinaryValueExpression div(ValueExpression l, ValueExpression r) { return new Div(l, r); }
    public static BinaryValueExpression mul(ValueExpression l, ValueExpression r) { return new Mul(l, r); }
    public static BinaryValueExpression sub(ValueExpression l, ValueExpression r) { return new Sub(l, r); }
    public static UnaryValueExpression neg(ValueExpression v) { return new Neg(v); }
    public static ValueExpression con(long v) { return new Con(new Value(BigInteger.valueOf(v).toByteArray())); }
    public static ValueExpression ref(String n) { return new Ref(n); }
    
    public static BinaryLogicalExpression and(Expression l, Expression r) { return new And(l, r); }
    public static BinaryLogicalExpression or(Expression l, Expression r) { return new Or(l, r); }
    public static UnaryLogicalExpression not(Expression e) { return new Not(e); }
    public static Expression expTrue() { return new True(); }
    
    public static ComparisonExpression eq(ValueExpression l, ValueExpression r) { return new Eq(l, r); }
    public static ComparisonExpression gt(ValueExpression l, ValueExpression r) { return new Gt(l, r); }
    public static ComparisonExpression lt(ValueExpression l, ValueExpression r) { return new Lt(l, r); }
    
}
