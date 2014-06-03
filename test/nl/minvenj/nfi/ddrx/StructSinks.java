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
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.Shorthand.str;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;

import java.io.IOException;
import java.util.List;

import nl.minvenj.nfi.ddrx.expression.value.Value;
import nl.minvenj.nfi.ddrx.token.StructSink;
import nl.minvenj.nfi.ddrx.token.Token;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class StructSinks {

    private final static Token TOKEN =
        str("test", seq(any("a"), any("b")), new StructSink() {
            @Override
            public void handleStruct(List<Value> struct) {
                Assert.assertEquals(2, struct.size());
                Assert.assertTrue(struct.get(0).getName().equals("a"));
                Assert.assertTrue(struct.get(1).getName().equals("b"));
            }
        });

    @Test
    public void structSinkSingle() throws IOException {
        TOKEN.parse(stream(1, 2), enc());
    }

    @Test
    public void structSinkMultiInRep() throws IOException {
        rep(TOKEN).parse(stream(1, 2, 3, 4), enc());
    }

    @Test
    public void structSinkMultiOverRep() throws IOException {
        str("outer", rep(TOKEN), new StructSink() {
            @Override
            public void handleStruct(List<Value> struct) {
                Assert.assertEquals(4, struct.size());
                for (int i = 0; i < struct.size(); i++) {
                    Assert.assertEquals((i & 1) == 0 ? "a" : "b", struct.get(i).getName());
                    Assert.assertEquals(i+1, struct.get(i).asNumeric().intValue());
                }
            }
        }).parse(stream(1, 2, 3, 4), enc());
    }

}
