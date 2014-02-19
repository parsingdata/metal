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

import static nl.minvenj.nfi.ddrx.util.Shorthand.cho;
import static nl.minvenj.nfi.ddrx.util.Shorthand.rep;
import static nl.minvenj.nfi.ddrx.util.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.util.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.util.TokenDefinitions.fixed;
import static nl.minvenj.nfi.ddrx.util.TokenDefinitions.stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.ddrx.token.Token;

@RunWith(JUnit4.class)
public class BackTrackOffset {

    private Token _backTrackChoice = cho(seq(any("a"), fixed("b", 2)),
                                         seq(any("c"), fixed("d", 3)));

    private Token _backTrackRepeat = seq(rep(seq(fixed("a", 1), fixed("b", 2))),
                                         seq(fixed("c", 1), fixed("d", 3)));

    private Token _backTrackDeepFragment = rep(seq(any("a"), seq(any("b"), cho(fixed("c", 21), fixed("d", 42)))));
    private Token _backTrackDeep = cho(seq(_backTrackDeepFragment, fixed("e", 63)),
                                       seq(_backTrackDeepFragment, fixed("f", 84)));

    @Test
    public void choiceLeft() {
        Assert.assertTrue(_backTrackChoice.eval(stream(1, 2)));
    }

    @Test
    public void choiceRight() {
        Assert.assertTrue(_backTrackChoice.eval(stream(1, 3)));
    }

    @Test
    public void choiceNone() {
        Assert.assertFalse(_backTrackChoice.eval(stream(1, 4)));
    }

    @Test
    public void repeatZero() {
        Assert.assertTrue(_backTrackRepeat.eval(stream(1, 3)));
    }

    @Test
    public void repeatOnce() {
        Assert.assertTrue(_backTrackRepeat.eval(stream(1, 2, 1, 3)));
    }

    @Test
    public void repeatTwice() {
        Assert.assertTrue(_backTrackRepeat.eval(stream(1, 2, 1, 2, 1, 3)));
    }

    @Test
    public void repeatNone() {
        Assert.assertFalse(_backTrackRepeat.eval(stream(1, 4)));
    }

    @Test
    public void deepMatch() {
        Assert.assertTrue(_backTrackDeep.eval(stream(1, 2, 21, 1, 2, 42, 1, 2, 21, 1, 2, 42, 1, 2, 21, 1, 2, 42, 84)));
    }

}
