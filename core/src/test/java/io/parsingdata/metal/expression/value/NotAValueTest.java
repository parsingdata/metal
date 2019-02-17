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

import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class NotAValueTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void getSlice() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("NOT_A_VALUE does not support any Value operation.");
        NOT_A_VALUE.slice();
    }

    @Test
    public void getEncoding() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("NOT_A_VALUE does not support any Value operation.");
        NOT_A_VALUE.encoding();
    }

    @Test
    public void getValue() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("NOT_A_VALUE does not support any Value operation.");
        NOT_A_VALUE.value();
    }

    @Test
    public void getLength() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("NOT_A_VALUE does not support any Value operation.");
        NOT_A_VALUE.length();
    }

    @Test
    public void asNumeric() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("NOT_A_VALUE does not support any Value operation.");
        NOT_A_VALUE.asNumeric();
    }

    @Test
    public void asString() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("NOT_A_VALUE does not support any Value operation.");
        NOT_A_VALUE.asString();
    }

    @Test
    public void asBitSet() {
        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("NOT_A_VALUE does not support any Value operation.");
        NOT_A_VALUE.asBitSet();
    }

}
