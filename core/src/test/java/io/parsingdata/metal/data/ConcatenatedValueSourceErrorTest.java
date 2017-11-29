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

package io.parsingdata.metal.data;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TEN;
import static java.math.BigInteger.ZERO;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConcatenatedValueSourceErrorTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void emptyConcatenatedValueSource() {
        final ConcatenatedValueSource cvs = new ConcatenatedValueSource(new ImmutableList<>(), TEN);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Data to read is not available (offset=0;length=1;source=ConcatenatedValueSource((10))).");
        Slice.createFromSource(cvs, ZERO, ONE).get().getData();
    }

}
