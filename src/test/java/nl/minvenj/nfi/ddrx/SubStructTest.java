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

import static nl.minvenj.nfi.ddrx.Shorthand.rep;
import static nl.minvenj.nfi.ddrx.Shorthand.sub;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;

import java.io.IOException;

import nl.minvenj.nfi.ddrx.data.ParseResult;
import nl.minvenj.nfi.ddrx.data.ParsedValueList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class SubStructTest {
    
    @Test
    public void offsetTable() throws IOException {
        ParseResult res = rep(sub(any("a"))).parse(stream(1, 3, 9, 5), enc());
        Assert.assertTrue(res.succeeded());
        traverse(5, res.getEnvironment().order);
    }
    
    private void traverse(int next, ParsedValueList list) {
        if (next == 0) {
            Assert.assertNull(list);
        } else {
            Assert.assertEquals(list.head.asNumeric().intValue(), next);
            traverse((int)list.head.getOffset(), list.tail);
        }
    }

}
