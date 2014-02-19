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

package nl.minvenj.nfi.ddrx.util;

import java.math.BigInteger;

import org.junit.Ignore;

import nl.minvenj.nfi.ddrx.expression.Expression;
import nl.minvenj.nfi.ddrx.expression.comparison.Equals;
import nl.minvenj.nfi.ddrx.expression.comparison.GreaterThan;
import nl.minvenj.nfi.ddrx.expression.comparison.LessThan;
import nl.minvenj.nfi.ddrx.expression.logical.And;
import nl.minvenj.nfi.ddrx.expression.logical.Not;
import nl.minvenj.nfi.ddrx.expression.logical.Or;
import nl.minvenj.nfi.ddrx.expression.value.Add;
import nl.minvenj.nfi.ddrx.expression.value.Con;
import nl.minvenj.nfi.ddrx.expression.value.Div;
import nl.minvenj.nfi.ddrx.expression.value.Mul;
import nl.minvenj.nfi.ddrx.expression.value.Neg;
import nl.minvenj.nfi.ddrx.expression.value.Ref;
import nl.minvenj.nfi.ddrx.expression.value.Sub;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;
import nl.minvenj.nfi.ddrx.token.Choice;
import nl.minvenj.nfi.ddrx.token.Repeat;
import nl.minvenj.nfi.ddrx.token.Sequence;
import nl.minvenj.nfi.ddrx.token.Token;
import nl.minvenj.nfi.ddrx.token.Value;

@Ignore
public class Shorthand {
    
    public static Token val(String name, ValueExpression size, Expression pred) { return new Value(name, size, pred); }
    public static Token cho(Token l, Token r) { return new Choice(l, r); }
    public static Token rep(Token t) { return new Repeat(t); }
    public static Token seq(Token l, Token r) { return new Sequence(l, r); }
    
    public static ValueExpression add(ValueExpression l, ValueExpression r) { return new Add(l, r); }
    public static ValueExpression div(ValueExpression l, ValueExpression r) { return new Div(l, r); }
    public static ValueExpression mul(ValueExpression l, ValueExpression r) { return new Mul(l, r); }
    public static ValueExpression sub(ValueExpression l, ValueExpression r) { return new Sub(l, r); }
    public static ValueExpression neg(ValueExpression v) { return new Neg(v); }
    public static ValueExpression con(long v) { return new Con(BigInteger.valueOf(v)); }
    public static ValueExpression ref(String n) { return new Ref(n); }
    
    public static Expression and(Expression l, Expression r) { return new And(l, r); }
    public static Expression or(Expression l, Expression r) { return new Or(l, r); }
    public static Expression not(Expression e) { return new Not(e); }
    
    public static Expression eq(ValueExpression l, ValueExpression r) { return new Equals(l, r); }
    public static Expression gt(ValueExpression l, ValueExpression r) { return new GreaterThan(l, r); }
    public static Expression lt(ValueExpression l, ValueExpression r) { return new LessThan(l, r); }
    
}
