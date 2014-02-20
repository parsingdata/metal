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

import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.ref;
import static nl.minvenj.nfi.ddrx.Shorthand.val;

import static nl.minvenj.nfi.ddrx.data.Environment.stream;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.ddrx.token.Token;

@RunWith(JUnit4.class)
public class Simple {

    private Token buildSimpleToken(String name, int size, String refName, int predicateSize) {
        return val(name,
                   con(size),
                   eq(ref(refName),
                      con(predicateSize)));
    }

    @Test
    public void correct() {
        Token t = buildSimpleToken("r1", 1, "r1", 1);
        Assert.assertTrue(t.eval(stream(1, 2, 3, 4)));
    }

    @Test
    public void sizeError() {
        Token t = buildSimpleToken("r1", 2, "r1", 1);
        Assert.assertFalse(t.eval(stream(1, 2, 3, 4)));
    }

    @Test(expected = NullPointerException.class)
    public void refError() {
        Token t = buildSimpleToken("r1", 1, "r2", 1);
        t.eval(stream(1, 2, 3, 4));
    }

    @Test
    public void predicateError() {
        Token t = buildSimpleToken("r1", 1, "r1", 2);
        Assert.assertFalse(t.eval(stream(1, 2, 3, 4)));
    }

    @Test
    public void sourceError() {
        Token t = buildSimpleToken("r1", 1, "r1", 1);
        Assert.assertFalse(t.eval(stream(2, 2, 2, 2)));
    }

}
