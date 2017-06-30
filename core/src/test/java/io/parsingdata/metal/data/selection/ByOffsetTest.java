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

import static org.junit.Assert.assertEquals;

import static io.parsingdata.metal.data.selection.ByOffset.findItemAtOffset;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.data.Source;
import io.parsingdata.metal.encoding.Encoding;

public class ByOffsetTest {

    private final Source source = new Source() { @Override protected byte[] getData(long offset, int size) throws IOException { return new byte[0]; } };

    @Test
    public void findItemAtOffsetTest() {
        assertEquals("the_one",
            findItemAtOffset(ImmutableList.create(ParseGraph.EMPTY.add(new ParseValue("two", any("a"), new Slice(source, 2, new byte[] { 1, 2 }), new Encoding()))
                                                                  .add(new ParseValue("zero", any("a"), new Slice(source, 0, new byte[] { 1, 2 }), new Encoding()))
                                                                  .add(new ParseValue("the_one", any("a"), new Slice(source, 1, new byte[] { 1, 2 }), new Encoding()))), 0, source).computeResult().get().asGraph().head.asValue().name);
    }

}
