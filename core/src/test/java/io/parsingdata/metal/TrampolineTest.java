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

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

import static org.junit.Assert.assertEquals;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.util.EncodingFactory.le;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.math.BigInteger;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.Trampoline.CompletedTrampoline;
import io.parsingdata.metal.Trampoline.IntermediateTrampoline;
import io.parsingdata.metal.token.Token;
public class TrampolineTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void resultOnIntermediateTrampoline() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("An IntermediateTrampoline does not have a result.");
        ((IntermediateTrampoline<Integer>) () -> null).result();
    }

    @Test
    public void nextOnFinalTrampoline() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("A CompletedTrampoline does not have a next computation.");
        ((CompletedTrampoline<Integer>) () -> 42).next();
    }

    @Test
    public void noStackOverflow() {
        // The 100000th Fibonacci number has 20899 digits
        assertEquals(20_899, fibonacci(ZERO, ONE, 100_000).computeResult().toString().length());
    }

    @Test
    public void testCho() {
        // This test fails because ParseGraph.addBranch() is recursive.
        // Note that ParseGraph.add() is also recursive and should be fixed.

        // Create nested cho
        final int[] values = new int[10_000];
        Token choice = def("0", 1, eqNum(con(0)));
        for (int i = 1; i < values.length; i++) {
            final int mod = i % 1;
            values[i] = mod;
            choice = cho(
                choice,
                def(Integer.toString(mod), 1, eqNum(con(mod))));
        }

        choice.parse(stream(values), le()).get();
    }

    private Trampoline<BigInteger> fibonacci(final BigInteger l, final BigInteger r, final long count) {
        if (count == 0) {
            return complete(() -> l);
        }
        return intermediate(() -> fibonacci(r, l.add(r), count-1));
    }

}
