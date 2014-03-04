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
import java.nio.charset.Charset;

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
import nl.minvenj.nfi.ddrx.expression.value.NumericValue;
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
    
    public static Token defNum(String name, ValueExpression<NumericValue> size, Expression pred) { return new Val<NumericValue>(name, size, pred, NumericValue.class); }
    public static Token defVal(String name, ValueExpression<NumericValue> size, Expression pred) { return new Val<Value>(name, size, pred, Value.class); }
    public static Token cho(Token l, Token r) { return new Cho(l, r); }
    public static Token rep(Token t) { return new Rep(t); }
    public static Token seq(Token l, Token r) { return new Seq(l, r); }
    
    public static BinaryValueExpression<NumericValue> add(ValueExpression<NumericValue> l, ValueExpression<NumericValue> r) { return new Add(l, r); }
    public static BinaryValueExpression<NumericValue> div(ValueExpression<NumericValue> l, ValueExpression<NumericValue> r) { return new Div(l, r); }
    public static BinaryValueExpression<NumericValue> mul(ValueExpression<NumericValue> l, ValueExpression<NumericValue> r) { return new Mul(l, r); }
    public static BinaryValueExpression<NumericValue> sub(ValueExpression<NumericValue> l, ValueExpression<NumericValue> r) { return new Sub(l, r); }
    public static UnaryValueExpression<NumericValue> neg(ValueExpression<NumericValue> v) { return new Neg(v); }
    public static ValueExpression<NumericValue> con(long v) { return new Con<NumericValue>(new NumericValue(BigInteger.valueOf(v))); }
    public static ValueExpression<Value> con(String s) { return new Con<Value>(new Value(s.getBytes(Charset.forName("ISO646-US")))); }
    public static ValueExpression<Value> refVal(String s) { return new Ref<Value>(s); }
    public static ValueExpression<NumericValue> refNum(String s) { return new Ref<NumericValue>(s); }
    
    public static BinaryLogicalExpression and(Expression l, Expression r) { return new And(l, r); }
    public static BinaryLogicalExpression or(Expression l, Expression r) { return new Or(l, r); }
    public static UnaryLogicalExpression not(Expression e) { return new Not(e); }
    public static Expression expTrue() { return new True(); }
    
    public static <T extends Value>ComparisonExpression<T> eq(ValueExpression<T> l, ValueExpression<T> r) { return new Eq<T>(l, r); }
    public static ComparisonExpression<NumericValue> gt(ValueExpression<NumericValue> l, ValueExpression<NumericValue> r) { return new Gt(l, r); }
    public static ComparisonExpression<NumericValue> lt(ValueExpression<NumericValue> l, ValueExpression<NumericValue> r) { return new Lt(l, r); }
    
}
