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

package io.parsingdata.metal.expression.value;

import static org.junit.Assert.assertEquals;

import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ConstantFactoryLongTest {

    public static final BigInteger TWOS_DIFF = BigInteger.valueOf(2).add(BigInteger.valueOf(Long.MAX_VALUE)).add(BigInteger.valueOf(Long.MAX_VALUE));

    private final long value;

    public ConstantFactoryLongTest(long value) {
        this.value = value;
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> arguments() {
        return Arrays.asList(new Object[][] {
            { 0L },
            { -128L }, { 127L },
            { -32768L }, { 32767L },
            { -8388608L }, { 8388607L },
            { -2147483648L }, { 2147483647L },
            { -549755813888L }, { 549755813887L },
            { -140737488355328L }, { 140737488355327L },
            { -36028797018963968L }, { 36028797018963967L },
            { -9223372036854775808L }, { 9223372036854775807L }
        });
    }

    @Test
    public void checkLong() {
        assertEquals(value, ConstantFactory.createFromNumeric(value, signed()).asNumeric().longValueExact());
        if (value >= 0) {
            assertEquals(value, ConstantFactory.createFromNumeric(value, enc()).asNumeric().longValueExact());
        } else {
            assertEquals(0, calculateUnsignedValue(value).compareTo(ConstantFactory.createFromNumeric(value, enc()).asNumeric()));
        }
    }

    private BigInteger calculateUnsignedValue(final long input) {
        for (int i = 8; i < 64; i+=8) {
            final long maxValue = BigInteger.valueOf(2).pow(i-1).longValueExact();
            if ((maxValue + input) >= 0) {
                return BigInteger.valueOf(input).add(BigInteger.valueOf(2*maxValue));
            }
        }
        return BigInteger.valueOf(input).add(TWOS_DIFF);
    }

}
