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

import nl.minvenj.nfi.ddrx.expression.True;
import nl.minvenj.nfi.ddrx.expression.comparison.Equals;
import nl.minvenj.nfi.ddrx.expression.value.Con;
import nl.minvenj.nfi.ddrx.expression.value.Ref;
import nl.minvenj.nfi.ddrx.io.ByteStream;
import nl.minvenj.nfi.ddrx.token.Choice;
import nl.minvenj.nfi.ddrx.token.Repeat;
import nl.minvenj.nfi.ddrx.token.Sequence;
import nl.minvenj.nfi.ddrx.token.Token;
import nl.minvenj.nfi.ddrx.token.Value;

@RunWith(JUnit4.class)
public class BackTrackOffset {

    private Token _backTrackChoice = new Choice(
                                                new Sequence(any("a"), fixed("b", 2)),
                                                new Sequence(any("c"), fixed("d", 3)));

    private Token _backTrackRepeat = new Sequence(
                                                  new Repeat(new Sequence(fixed("a", 1), fixed("b", 2))),
                                                  new Sequence(fixed("c", 1), fixed("d", 3)));

    private Token any(String name) {
        return new Value(name, new Con(BigInteger.valueOf(1)), new True());
    }

    private Token fixed(String name, int value) {
        return new Value(name, new Con(BigInteger.valueOf(1)), new Equals(new Ref(name), new Con(BigInteger.valueOf(value))));
    }
    
    private ByteStream stream(int... bytes) {
        byte[] out = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            out[i] = (byte)bytes[i];
        }
        return new ByteStream(out);
    }

    @Test
    public void choiceLeft() {
        Assert.assertTrue(_backTrackChoice.eval(stream(1, 2)));
    }

    @Test
    public void choiceRight() {
        Assert.assertTrue(_backTrackChoice.eval(stream(1, 3)));
    }
    
    @Test
    public void choiceNone() {
        Assert.assertFalse(_backTrackChoice.eval(stream(1, 4)));
    }
    
    @Test
    public void repeatZero() {
        Assert.assertTrue(_backTrackRepeat.eval(stream(1, 3)));
    }

    @Test
    public void repeatOnce() {
        Assert.assertTrue(_backTrackRepeat.eval(stream(1, 2, 1, 3)));
    }

    @Test
    public void repeatTwice() {
        Assert.assertTrue(_backTrackRepeat.eval(stream(1, 2, 1, 2, 1, 3)));
    }

    @Test
    public void repeatNone() {
        Assert.assertFalse(_backTrackRepeat.eval(stream(1, 4)));
    }

}
