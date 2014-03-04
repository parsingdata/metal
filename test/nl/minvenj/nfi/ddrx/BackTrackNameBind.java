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

import static nl.minvenj.nfi.ddrx.Shorthand.cho;
import static nl.minvenj.nfi.ddrx.Shorthand.rep;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.eqRefNum;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.eqNum;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.notEqRefNum;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static nl.minvenj.nfi.ddrx.data.Environment.stream;
import nl.minvenj.nfi.ddrx.token.Token;

@RunWith(JUnit4.class)
public class BackTrackNameBind {

    private Token _choiceRef = seq(any("a"),
                                   cho(seq(any("a"), eqRefNum("b", "a")),
                                       seq(notEqRefNum("b", "a"), any("c"))));

    private Token _repeatRef = seq(rep(eqNum("a", 42)),
                                   rep(notEqRefNum("b", "a")));

    @Test
    public void choiceRefLeft() {
        Assert.assertTrue(_choiceRef.parse(stream(1, 2, 2)));
    }

    @Test
    public void choiceRefRight() {
        Assert.assertTrue(_choiceRef.parse(stream(1, 2, 3)));
    }

    @Test
    public void choiceRefNone() {
        Assert.assertFalse(_choiceRef.parse(stream(1, 1, 2)));
    }

    @Test
    public void repeatRef() {
        Assert.assertTrue(_repeatRef.parse(stream(42, 42, 42, 21, 21, 21)));
    }

}
