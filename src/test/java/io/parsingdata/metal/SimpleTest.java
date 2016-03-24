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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import java.io.IOException;

import io.parsingdata.metal.token.Token;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SimpleTest {

    private Token buildSimpleToken(final String name, final int size, final int predicate) {
        return def(name, con(size), eq(con(predicate)));
    }

    @Test
    public void correct() throws IOException {
        final Token t = buildSimpleToken("r1", 1, 1);
        Assert.assertTrue(t.parse(stream(1, 2, 3, 4), enc()).succeeded());
    }

    @Test
    public void sizeError() throws IOException {
        final Token t = buildSimpleToken("r1", 2, 1);
        Assert.assertFalse(t.parse(stream(1, 2, 3, 4), enc()).succeeded());
    }

    @Test
    public void predicateError() throws IOException {
        final Token t = buildSimpleToken("r1", 1, 2);
        Assert.assertFalse(t.parse(stream(1, 2, 3, 4), enc()).succeeded());
    }

    @Test
    public void sourceError() throws IOException {
        final Token t = buildSimpleToken("r1", 1, 1);
        Assert.assertFalse(t.parse(stream(2, 2, 2, 2), enc()).succeeded());
    }

}
