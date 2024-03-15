/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

package io.parsingdata.metal.format;

import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Collection;
import java.util.List;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.util.ParameterizedParse;

public class FormatTest extends ParameterizedParse {

    @Override
    public Collection<Object[]> data() {
        return List.of(new Object[][] {
            { "PNG", PNG.FORMAT, parseState("/test.png"), enc(), true },
            { "ZIP", ZIP.FORMAT, parseState("/singlefile-zip30-ubuntu.zip"), enc(), true },
            { "ZIP2", ZIP.FORMAT, parseState("/multifile-zip30-ubuntu.zip"), enc(), true },
            { "JPEG", JPEG.FORMAT, parseState("/test.jpg"), enc(), true },
        });
    }

    private static ParseState parseState(final String path) {
        try {
            return stream(FormatTest.class.getResource(path).toURI());
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }

}
