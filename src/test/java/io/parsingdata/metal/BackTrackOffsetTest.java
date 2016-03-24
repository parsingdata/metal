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

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.TokenDefinitions.any;
import static io.parsingdata.metal.TokenDefinitions.eq;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import java.io.IOException;

import io.parsingdata.metal.token.Token;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BackTrackOffsetTest {

    private final Token _backTrackChoice = cho(seq(any("a"),
                                                   eq("b", 2)),
                                               seq(any("c"),
                                                   eq("d", 3)));

    private final Token _backTrackRepeat = seq(rep(seq(eq("a", 1),
                                                       eq("b", 2))),
                                               seq(eq("c", 1),
                                                   eq("d", 3)));

    private final Token _backTrackDeepFragment = rep(seq(any("a"),
                                                         any("b"),
                                                         cho(eq("c", 21),
                                                             eq("d", 42))));
    private final Token _backTrackDeep = cho(seq(_backTrackDeepFragment,
                                                 eq("e", 63)),
                                             seq(_backTrackDeepFragment,
                                                 eq("f", 84)));

    @Test
    public void choiceLeft() throws IOException {
        Assert.assertTrue(_backTrackChoice.parse(stream(1, 2), enc()).succeeded());
    }

    @Test
    public void choiceRight() throws IOException {
        Assert.assertTrue(_backTrackChoice.parse(stream(1, 3), enc()).succeeded());
    }

    @Test
    public void choiceNone() throws IOException {
        Assert.assertFalse(_backTrackChoice.parse(stream(1, 4), enc()).succeeded());
    }

    @Test
    public void repeatZero() throws IOException {
        Assert.assertTrue(_backTrackRepeat.parse(stream(1, 3), enc()).succeeded());
    }

    @Test
    public void repeatOnce() throws IOException {
        Assert.assertTrue(_backTrackRepeat.parse(stream(1, 2, 1, 3), enc()).succeeded());
    }

    @Test
    public void repeatTwice() throws IOException {
        Assert.assertTrue(_backTrackRepeat.parse(stream(1, 2, 1, 2, 1, 3), enc()).succeeded());
    }

    @Test
    public void repeatNone() throws IOException {
        Assert.assertFalse(_backTrackRepeat.parse(stream(1, 4), enc()).succeeded());
    }

    @Test
    public void deepMatch() throws IOException {
        Assert.assertTrue(_backTrackDeep.parse(stream(1, 2, 21, 1, 2, 42, 1, 2, 21, 1, 2, 42, 1, 2, 21, 1, 2, 42, 84), enc()).succeeded());
    }

}
