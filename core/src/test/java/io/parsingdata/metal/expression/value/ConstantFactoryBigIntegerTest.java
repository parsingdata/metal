/*
 * Copyright 2013-2020 Netherlands Forensic Institute
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
public class ConstantFactoryBigIntegerTest {

    private final BigInteger value;

    public ConstantFactoryBigIntegerTest(String value) {
        this.value = new BigInteger(value);
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> arguments() {
        return Arrays.asList(new Object[][] {
            { "0" },
            { "100000000000000000000" },
            { "-100000000000000000000" },
            { "12345678901234567890123456789012345678901234567890123456789012345678901234567890" },
        });
    }

    @Test
    public void checkBigInteger() {
        assertEquals(0, value.compareTo(ConstantFactory.createFromNumeric(value, signed()).asNumeric()));
        assertEquals(0, new BigInteger(1, value.toByteArray()).compareTo(ConstantFactory.createFromNumeric(value, enc()).asNumeric()));
    }

}
