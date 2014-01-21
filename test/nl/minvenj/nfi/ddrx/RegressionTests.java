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
    
    @Test
    public void simple1Correct() {
        Token t = new Value("r1", 
                            new Con(BigInteger.valueOf(1)), 
                            new Equals(
                                       new Ref("r1"), 
                                       new Con(BigInteger.valueOf(1))), 
                            new ByteStream(_input));
        org.junit.Assert.assertTrue(t.eval());
    }
    
    @Test(expected=NullPointerException.class)
    public void simple1RefError() {
        Token t = new Value("r1", 
                            new Con(BigInteger.valueOf(1)), 
                            new Equals(
                                       new Ref("r2"), 
                                       new Con(BigInteger.valueOf(1))), 
                            new ByteStream(_input));
        t.eval();
    }
    
    @Test
    public void simple1SizeError() {
        Token t = new Value("r1", 
                            new Con(BigInteger.valueOf(2)), 
                            new Equals(
                                       new Ref("r1"), 
                                       new Con(BigInteger.valueOf(1))), 
                            new ByteStream(_input));
        org.junit.Assert.assertFalse(t.eval());
    }
    
    @Test
    public void simple1PredicateError() {
        Token t = new Value("r1", 
                            new Con(BigInteger.valueOf(1)), 
                            new Equals(
                                       new Ref("r1"), 
                                       new Con(BigInteger.valueOf(2))), 
                            new ByteStream(_input));
        org.junit.Assert.assertFalse(t.eval());
    }
    
    @Test
    public void simple1SourceError() {
        Token t = new Value("r1", 
                            new Con(BigInteger.valueOf(1)), 
                            new Equals(
                                       new Ref("r1"), 
                                       new Con(BigInteger.valueOf(1))), 
                            new ByteStream(new byte[] { 2, 2, 2, 2 }));
        org.junit.Assert.assertFalse(t.eval());
    }
    
}
