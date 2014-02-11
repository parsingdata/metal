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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.ddrx.expression.comparison.Equals;
import nl.minvenj.nfi.ddrx.expression.logical.Not;
import nl.minvenj.nfi.ddrx.expression.value.Con;
import nl.minvenj.nfi.ddrx.expression.value.Ref;
import nl.minvenj.nfi.ddrx.io.ByteStream;
import nl.minvenj.nfi.ddrx.token.Repeat;
import nl.minvenj.nfi.ddrx.token.Sequence;
import nl.minvenj.nfi.ddrx.token.Token;
import nl.minvenj.nfi.ddrx.token.Value;

@SuppressWarnings("resource")
@RunWith(JUnit4.class)
public class ReadUntil {
    
    private Token _readUntil = new Sequence(
                                            new Repeat(new Value("any", new Con(BigInteger.valueOf(1)), new Not(new Equals(new Ref("any"), new Con(BigInteger.valueOf(42)))))),
                                            new Value("terminator", new Con(BigInteger.valueOf(1)), new Equals(new Ref("terminator"), new Con(BigInteger.valueOf(42)))));
    
    @Test
    public void readUntilConstant() {
        Assert.assertTrue(_readUntil.eval(new ByteStream(new byte[] { 1, 2, 3, 4, 42 })));
    }
    
    @Test
    public void readUntilNoSkipping() {
        Assert.assertTrue(_readUntil.eval(new ByteStream(new byte[] { 42 })));
    }
    
    @Test
    public void readUntilErrorNoTerminator() {
        Assert.assertFalse(_readUntil.eval(new ByteStream(new byte[] { 1, 2, 3, 4 })));
    }
    
}
