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

package io.parsingdata.metal.expression.value;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ConstantFactoryBigIntegerTest {

    public static Collection<Object[]> arguments() {
        return List.of(new Object[][] {
            { "0" },
            { "100000000000000000000" },
            { "-100000000000000000000" },
            { "12345678901234567890123456789012345678901234567890123456789012345678901234567890" },
        });
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("arguments")
    public void checkBigInteger(final BigInteger value) {
        assertEquals(0, value.compareTo(ConstantFactory.createFromNumeric(value, signed()).asNumeric()));
        assertEquals(0, new BigInteger(1, value.toByteArray()).compareTo(ConstantFactory.createFromNumeric(value, enc()).asNumeric()));
    }

}
