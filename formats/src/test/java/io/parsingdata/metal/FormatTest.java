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

package io.parsingdata.metal;

import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.parsingdata.metal.format.JPEG;
import io.parsingdata.metal.format.PNG;
import io.parsingdata.metal.format.ZIP;

@RunWith(JUnit4.class)
public class FormatTest {

    private static final String PNGFILE = "/test.png";
    private static final String ZIPFILE1 = "/singlefile-zip30-ubuntu.zip";
    private static final String ZIPFILE2 = "/multifile-zip30-ubuntu.zip";
    private static final String JPEGFILE = "/test.jpg";

    @Test
    public void parsePNG() throws IOException, URISyntaxException {
        assertTrue(PNG.FORMAT.parse(stream(toURI(PNGFILE)), enc()).succeeded);
    }

    @Test
    public void parseZIP() throws IOException, URISyntaxException {
        assertTrue(ZIP.FORMAT.parse(stream(toURI(ZIPFILE1)), enc()).succeeded);
    }

    @Test
    public void parseZIP2() throws IOException, URISyntaxException {
        assertTrue(ZIP.FORMAT.parse(stream(toURI(ZIPFILE2)), enc()).succeeded);
    }

    @Test
    public void parseJPEG() throws IOException, URISyntaxException {
        assertTrue(JPEG.FORMAT.parse(stream(toURI(JPEGFILE)), enc()).succeeded);
    }

    private URI toURI(final String resource) throws URISyntaxException {
        return getClass().getResource(resource).toURI();
    }

}
