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

import static nl.minvenj.nfi.ddrx.Shorthand.and;
import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.def;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.self;
import static nl.minvenj.nfi.ddrx.Shorthand.shr;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.le;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import nl.minvenj.nfi.ddrx.token.Token;

public class EndiannessTest {

    @Test
    public void andAcrossByteBoundaryLE() throws IOException {
        final Token t = def("x", con(2), eq(and(self, con(0x03, 0xff)), con(0x01, 0x1b)));
        Assert.assertTrue(t.parse(stream(0x1b, 0x81), le()).succeeded());
    }

    @Test
    public void constructIntermediateConstantLE() throws IOException {
        final Token t = def("x", con(2), eq(and(shr(con(0x82, 0x1b), con(1)), con(0x03, 0xff)), con(0x01, 0x0d)));
        Assert.assertTrue(t.parse(stream(0x00, 0x00), le()).succeeded());
    }

}
