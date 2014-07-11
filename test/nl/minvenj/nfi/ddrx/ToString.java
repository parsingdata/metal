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

import static nl.minvenj.nfi.ddrx.Shorthand.*;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import nl.minvenj.nfi.ddrx.expression.Expression;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;
import nl.minvenj.nfi.ddrx.token.Token;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ToString {
    
    private static final String prefix = "prefix";
    private int count;
    
    @Before
    public void before() {
        count = 0;
    }
    
    @Test
    public void validateToStringImplementation() {
        Expression e = not(and(eq(v()), or(eqNum(v()), and(eqStr(v()), or(gtNum(v()), ltNum(v()))))));
        Token t = str("str", rep(cho(any(n()), seq(any(n()), def(n(), con(1), e)))));
        String output = t.toString();
        for (int i = 0; i < count; i++) {
            Assert.assertTrue(output.contains(prefix + i));
        }
    }
    
    private String n() {
        return prefix + count++;
    }
    
    private ValueExpression v() {
        return neg(add(div(mod(mul(sub(ref(n()), first(n())), con(1)), cat(ref(n()), ref(n()))), self), ref(n())));
    }

}
