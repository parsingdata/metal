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

package io.parsingdata.metal.format;

import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.util.ParameterizedParse;

public class FormatTest extends ParameterizedParse {

    @Parameterized.Parameters(name="{0} ({4})")
    public static Collection<Object[]> data() throws URISyntaxException, IOException {
        return Arrays.asList(new Object[][] {
            { "PNG", PNG.FORMAT, env("/test.png"), enc(), true },
            { "ZIP", ZIP.FORMAT, env("/singlefile-zip30-ubuntu.zip"), enc(), true },
            { "ZIP2", ZIP.FORMAT, env("/multifile-zip30-ubuntu.zip"), enc(), true },
            { "JPEG", JPEG.FORMAT, env("/test.jpg"), enc(), true },
        });
    }

    private static Environment env(final String path) throws URISyntaxException, IOException {
        return stream(FormatTest.class.getClass().getResource(path).toURI());
    }

}
