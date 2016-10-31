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

package io.parsingdata.metal.token;

import static org.junit.Assert.assertEquals;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Test;

public class DefTest {

    @Test
    public void testScopeWithoutEncoding() throws IOException {
        assertEquals(1, getValue(def("a", 1).parse("scope", stream(1), enc()).environment.order, "scope.a").asNumeric().intValue());
    }

    @Test
    public void testScopeWithEncoding() throws IOException {
        assertEquals(1, getValue(def("a", 1, signed()).parse("scope", stream(1), enc()).environment.order, "scope.a").asNumeric().intValue());
    }
}
