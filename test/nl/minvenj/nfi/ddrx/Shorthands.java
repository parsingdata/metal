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

import static nl.minvenj.nfi.ddrx.Shorthand.*;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.ddrx.token.Token;

@RunWith(JUnit4.class)
public class Shorthands {

    private final Token multiSequence = seq(
                                            def("a", con(1), eq(con(1))),
                                            def("b", con(1), eq(con(2))),
                                            def("c", con(1), eq(con(3))));

    private final Token multiChoice = cho(
                                          def("a", con(1), eq(con(1))),
                                          def("b", con(1), eq(con(2))),
                                          def("c", con(1), eq(con(3))));

    @Test
    public void sequenceMultiMatch() throws IOException {
        Assert.assertTrue(multiSequence.parse(stream(1, 2, 3), enc()));
    }

    @Test
    public void sequenceMultiNoMatch() throws IOException {
        Assert.assertFalse(multiSequence.parse(stream(1, 2, 2), enc()));
    }

    @Test
    public void choiceMultiMatch() throws IOException {
        Assert.assertTrue(multiChoice.parse(stream(3, 2, 1), enc()));
    }

    @Test
    public void choiceMultiNoMatch() throws IOException {
        Assert.assertFalse(multiChoice.parse(stream(42, 63, 84), enc()));
    }

}
