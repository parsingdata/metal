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

package nl.minvenj.nfi.metal;

import static nl.minvenj.nfi.metal.Shorthand.cho;
import static nl.minvenj.nfi.metal.Shorthand.con;
import static nl.minvenj.nfi.metal.Shorthand.def;
import static nl.minvenj.nfi.metal.Shorthand.eq;
import static nl.minvenj.nfi.metal.Shorthand.gtNum;
import static nl.minvenj.nfi.metal.Shorthand.seq;
import static nl.minvenj.nfi.metal.util.EncodingFactory.enc;
import static nl.minvenj.nfi.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.metal.data.ParseResult;
import nl.minvenj.nfi.metal.token.Token;

@RunWith(JUnit4.class)
public class ShorthandsTest {

    private final Token multiSequence = seq(def("a", con(1), eq(con(1))),
                                            def("b", con(1), eq(con(2))),
                                            def("c", con(1), eq(con(3))));

    private final Token multiChoice = cho(def("a", con(1), gtNum(con(2))),
                                          def("b", con(1), gtNum(con(1))),
                                          def("c", con(1), gtNum(con(0))));

    @Test
    public void sequenceMultiMatch() throws IOException {
        Assert.assertTrue(multiSequence.parse(stream(1, 2, 3), enc()).succeeded());
    }

    @Test
    public void sequenceMultiNoMatch() throws IOException {
        Assert.assertFalse(multiSequence.parse(stream(1, 2, 2), enc()).succeeded());
    }

    @Test
    public void choiceMultiMatchA() throws IOException {
        runChoice(3, "a");
    }

    @Test
    public void choiceMultiMatchB() throws IOException {
        runChoice(2, "b");
    }

    @Test
    public void choiceMultiMatchC() throws IOException {
        runChoice(1, "c");
    }

    private void runChoice(final int data, final String matched) throws IOException {
        final ParseResult res = multiChoice.parse(stream(data), enc());
        Assert.assertTrue(res.succeeded());
        Assert.assertTrue(res.getEnvironment().order.flatten().current().getName().equals(matched));
    }

    @Test
    public void choiceMultiNoMatch() throws IOException {
        Assert.assertFalse(multiChoice.parse(stream(0), enc()).succeeded());
    }

}
