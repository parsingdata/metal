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
import static nl.minvenj.nfi.metal.Shorthand.rep;
import static nl.minvenj.nfi.metal.Shorthand.seq;
import static nl.minvenj.nfi.metal.TokenDefinitions.any;
import static nl.minvenj.nfi.metal.TokenDefinitions.eq;
import static nl.minvenj.nfi.metal.TokenDefinitions.eqRef;
import static nl.minvenj.nfi.metal.TokenDefinitions.notEqRef;
import static nl.minvenj.nfi.metal.util.EnvironmentFactory.stream;
import static nl.minvenj.nfi.metal.util.EncodingFactory.enc;

import java.io.IOException;

import nl.minvenj.nfi.metal.token.Token;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BackTrackNameBindTest {

    private final Token _choiceRef = seq(any("a"),
                                         cho(seq(any("a"), eqRef("b", "a")),
                                             seq(notEqRef("b", "a"), any("c"))));

    private final Token _repeatRef = seq(rep(eq("a", 42)),
                                         rep(notEqRef("b", "a")));

    @Test
    public void choiceRefLeft() throws IOException {
        Assert.assertTrue(_choiceRef.parse(stream(1, 2, 2), enc()).succeeded());
    }

    @Test
    public void choiceRefRight() throws IOException {
        Assert.assertTrue(_choiceRef.parse(stream(1, 2, 3), enc()).succeeded());
    }

    @Test
    public void choiceRefNone() throws IOException {
        Assert.assertFalse(_choiceRef.parse(stream(1, 1, 2), enc()).succeeded());
    }

    @Test
    public void repeatRef() throws IOException {
        Assert.assertTrue(_repeatRef.parse(stream(42, 42, 42, 21, 21, 21), enc()).succeeded());
    }

}
