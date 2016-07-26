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

import io.parsingdata.metal.data.ParseRef;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static io.parsingdata.metal.data.ParseGraph.EMPTY;
import static io.parsingdata.metal.data.ParseGraph.NONE;
import static io.parsingdata.metal.data.selection.ByType.getRefs;

public class ByTypeTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void unresolvableRef() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("A ref must point to an existing graph.");
        getRefs(EMPTY.add(new ParseRef(0, NONE)));
    }

}
