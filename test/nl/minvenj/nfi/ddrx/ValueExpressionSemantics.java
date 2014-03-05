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

import static nl.minvenj.nfi.ddrx.Shorthand.cat;
import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.defVal;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.refVal;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.data.Environment.stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.ddrx.token.Token;

@RunWith(JUnit4.class)
public class ValueExpressionSemantics {
    
    @Test
    public void Cat() {
        Token cat = seq(any("a"),
                        seq(any("b"),
                            defVal("c", con(2), eq(cat(refVal("a"), refVal("b"))))));
        Assert.assertTrue(cat.parse(stream(1, 2, 1, 2)));
        Assert.assertFalse(cat.parse(stream(1, 2, 12, 12)));
    }
    
}
