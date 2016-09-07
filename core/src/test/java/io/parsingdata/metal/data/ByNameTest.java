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

package io.parsingdata.metal.data;

import io.parsingdata.metal.data.selection.ByName;
import junit.framework.TestCase;
import org.junit.Test;

import static junit.framework.TestCase.assertNull;

public class ByNameTest {

    @Test
    public void getValueOnEmpty() {
        TestCase.assertNull(ByName.getValue(ParseGraph.EMPTY, "name"));
    }

    @Test
    public void getValueOnBranchedEmpty() {
        assertNull(ByName.getValue(ParseGraph.EMPTY.addBranch(ParseGraph.NONE), "name"));
    }

}