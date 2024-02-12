/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;

import java.math.BigInteger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.parsingdata.metal.Trampoline.CompletedTrampoline;
import io.parsingdata.metal.Trampoline.IntermediateTrampoline;

public class TrampolineTest {

    @Test
    public void resultOnIntermediateTrampoline() {
        final Exception e = Assertions.assertThrows(UnsupportedOperationException.class, () -> ((IntermediateTrampoline<Integer>) () -> null).result());
        assertEquals("An IntermediateTrampoline does not have a result.", e.getMessage());
    }

    @Test
    public void nextOnFinalTrampoline() {
        final Exception e = Assertions.assertThrows(UnsupportedOperationException.class, () -> ((CompletedTrampoline<Integer>) () -> 42).next());
        assertEquals("A CompletedTrampoline does not have a next computation.", e.getMessage());
    }

    @Test
    public void noStackOverflow() {
        // The 100000th Fibonacci number has 20899 digits
        assertEquals(20899, fibonacci(ZERO, ONE, 100000).computeResult().toString().length());
    }

    private Trampoline<BigInteger> fibonacci(final BigInteger l, final BigInteger r, final long count) {
        if (count == 0) {
            return complete(() -> l);
        }
        return intermediate(() -> fibonacci(r, l.add(r), count-1));
    }

}
