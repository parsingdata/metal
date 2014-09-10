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

import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.def;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;

import java.io.IOException;

import nl.minvenj.nfi.ddrx.token.Token;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SimpleTest {

    private Token buildSimpleToken(String name, int size, int predicate) {
        return def(name, con(size), eq(con(predicate)));
    }

    @Test
    public void correct() throws IOException {
        Token t = buildSimpleToken("r1", 1, 1);
        Assert.assertTrue(t.parse(stream(1, 2, 3, 4), enc()));
    }

    @Test
    public void sizeError() throws IOException {
        Token t = buildSimpleToken("r1", 2, 1);
        Assert.assertFalse(t.parse(stream(1, 2, 3, 4), enc()));
    }

    @Test
    public void predicateError() throws IOException {
        Token t = buildSimpleToken("r1", 1, 2);
        Assert.assertFalse(t.parse(stream(1, 2, 3, 4), enc()));
    }

    @Test
    public void sourceError() throws IOException {
        Token t = buildSimpleToken("r1", 1, 1);
        Assert.assertFalse(t.parse(stream(2, 2, 2, 2), enc()));
    }

}
