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

import static nl.minvenj.nfi.ddrx.util.TokenDefinitions.env;

import static nl.minvenj.nfi.ddrx.util.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.util.TokenDefinitions.fixed;
import static nl.minvenj.nfi.ddrx.util.TokenDefinitions.stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.ddrx.token.Choice;
import nl.minvenj.nfi.ddrx.token.Repeat;
import nl.minvenj.nfi.ddrx.token.Sequence;
import nl.minvenj.nfi.ddrx.token.Token;

@RunWith(JUnit4.class)
public class BackTrackOffset {

    private Token _backTrackChoice = new Choice(
                                                new Sequence(any("a"), fixed("b", 2)),
                                                new Sequence(any("c"), fixed("d", 3)));

    private Token _backTrackRepeat = new Sequence(
                                                  new Repeat(new Sequence(fixed("a", 1), fixed("b", 2))),
                                                  new Sequence(fixed("c", 1), fixed("d", 3)));
    
    private Token _backTrackDeepFragment = new Repeat(new Sequence(any("a"),new Sequence(any("b"), new Choice(fixed("c", 21), fixed("d", 42)))));
    private Token _backTrackDeep = new Choice(
    		                                  new Sequence(_backTrackDeepFragment, fixed("e", 63)),
    		                                  new Sequence(_backTrackDeepFragment, fixed("f", 84)));
    
    @Test
    public void choiceLeft() {
        Assert.assertTrue(_backTrackChoice.eval(stream(1, 2), env()));
    }

    @Test
    public void choiceRight() {
        Assert.assertTrue(_backTrackChoice.eval(stream(1, 3), env()));
    }
    
    @Test
    public void choiceNone() {
        Assert.assertFalse(_backTrackChoice.eval(stream(1, 4), env()));
    }
    
    @Test
    public void repeatZero() {
        Assert.assertTrue(_backTrackRepeat.eval(stream(1, 3), env()));
    }

    @Test
    public void repeatOnce() {
        Assert.assertTrue(_backTrackRepeat.eval(stream(1, 2, 1, 3), env()));
    }

    @Test
    public void repeatTwice() {
        Assert.assertTrue(_backTrackRepeat.eval(stream(1, 2, 1, 2, 1, 3), env()));
    }

    @Test
    public void repeatNone() {
        Assert.assertFalse(_backTrackRepeat.eval(stream(1, 4), env()));
    }
    
    @Test
    public void deepMatch() {
    	Assert.assertTrue(_backTrackDeep.eval(stream(1, 2, 21, 1, 2, 42, 1, 2, 21, 1, 2, 42, 1, 2, 21, 1, 2, 42, 84), env()));
    }
    
}
