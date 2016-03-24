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

import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.TokenDefinitions.eq;
import static io.parsingdata.metal.TokenDefinitions.notEq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import io.parsingdata.metal.token.Token;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ReadUntilTest {

    private final Token _readUntil = seq(rep(notEq("other", 42)),
                                         eq("terminator", 42));

    @Test
    public void readUntilConstant() throws IOException {
        Assert.assertTrue(_readUntil.parse(stream(1, 2, 3, 4, 42), enc()).succeeded());
    }

    @Test
    public void readUntilNoSkipping() throws IOException {
        Assert.assertTrue(_readUntil.parse(stream(42), enc()).succeeded());
    }

    @Test
    public void readUntilErrorNoTerminator() throws IOException {
        Assert.assertFalse(_readUntil.parse(stream(1, 2, 3, 4), enc()).succeeded());
    }

}
