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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.ddrx.expression.comparison.Equals;
import nl.minvenj.nfi.ddrx.expression.value.Con;
import nl.minvenj.nfi.ddrx.expression.value.Ref;
import nl.minvenj.nfi.ddrx.io.ByteStream;
import nl.minvenj.nfi.ddrx.token.Token;
import nl.minvenj.nfi.ddrx.token.Value;

@RunWith(JUnit4.class)
public class RegressionTests {
    
    private final byte[] _input = new byte[] { 1, 2, 3, 4 };
    
    private Value buildSimpleToken(String name, int size, String refName, int predicateSize, byte[] inputData) {
        return new Value(name,
                            new Con(BigInteger.valueOf(size)),
                            new Equals(
                                       new Ref(refName),
                                       new Con(BigInteger.valueOf(predicateSize))),
                            new ByteStream(inputData));
    }
    
    @Test
    public void simple1Correct() {
        Token t = buildSimpleToken("r1", 1, "r1", 1, _input);
        org.junit.Assert.assertTrue(t.eval());
    }

    @Test
    public void simple1SizeError() {
        Token t = buildSimpleToken("r1", 2, "r1", 1, _input);
        org.junit.Assert.assertFalse(t.eval());
    }
    
    @Test(expected=NullPointerException.class)
    public void simple1RefError() {
        Token t = buildSimpleToken("r1", 1, "r2", 1, _input);
        t.eval();
    }
    
    @Test
    public void simple1PredicateError() {
        Token t = buildSimpleToken("r1", 1, "r1", 2, _input);
        org.junit.Assert.assertFalse(t.eval());
    }
    
    @Test
    public void simple1SourceError() {
        Token t = buildSimpleToken("r1", 1, "r1", 1, new byte[] { 2, 2, 2, 2 });
        org.junit.Assert.assertFalse(t.eval());
    }
    
}
