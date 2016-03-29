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

package io.parsingdata.metal.data.selection;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ByTokenTest {

    private static final Token DEF1 = def("value1", con(1));
    private static final Token DEF2 = def("value2", con(1));
    private static Token SIMPLE_SEQ = seq(DEF1, DEF2);

    private static final Token UNUSED_DEF = def("value1", con(1));

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private ParseResult parseResult;

    @Before
    public void setUp() throws IOException {
        this.parseResult = SIMPLE_SEQ.parse(stream(0, 1), enc());
    }

    private ParseGraph parseResultGraph() {
        return parseResult.getEnvironment().order;
    }

    @Test
    public void nullCheck() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument definition may not be null");

        ByToken.get(parseResultGraph(), null);
    }

    @Test
    public void findRootToken() {
        final ParseItem parseItem = ByToken.get(parseResultGraph(), SIMPLE_SEQ);

        assertThat(parseItem.getDefinition(), is(equalTo(SIMPLE_SEQ)));
    }

    @Test
    public void findNestedToken() {
        final ParseItem parseItem = ByToken.get(parseResultGraph(), DEF1);

        assertThat(parseItem.getDefinition(), is(equalTo(DEF1)));
    }

    @Test
    public void findUnusedToken() {
        final ParseItem parseItem = ByToken.get(parseResultGraph(), UNUSED_DEF);

        assertThat(parseItem, is(nullValue()));
    }
}
