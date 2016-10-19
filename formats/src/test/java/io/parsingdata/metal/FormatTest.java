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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.format.UID.guid;
import static io.parsingdata.metal.format.UID.uuid;
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
import io.parsingdata.metal.token.Token;

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

    @Test
    public void parseGUID() throws IOException, URISyntaxException {
        final Token guid = def("guid", 16, guid("2dc27766-f623-4200-9d64-115e9bfd4a08"));
        assertFalse(guid.parse(stream(0x2d, 0xc2, 0x77, 0x66, 0xf6, 0x23, 0x42, 0x00, 0x9d, 0x64, 0x11, 0x5e, 0x9b, 0xfd, 0x4a, 0x08), enc()).succeeded);
        assertTrue(guid.parse(stream(0x66, 0x77, 0xc2, 0x2d, 0x23, 0xf6, 0x00, 0x42, 0x9d, 0x64, 0x11, 0x5e, 0x9b, 0xfd, 0x4a, 0x08), enc()).succeeded);
    }

    @Test
    public void parseUUID() throws IOException, URISyntaxException {
        final Token guid = def("guid", 16, uuid("2dc27766-f623-4200-9d64-115e9bfd4a08"));
        assertTrue(guid.parse(stream(0x2d, 0xc2, 0x77, 0x66, 0xf6, 0x23, 0x42, 0x00, 0x9d, 0x64, 0x11, 0x5e, 0x9b, 0xfd, 0x4a, 0x08), enc()).succeeded);
        assertFalse(guid.parse(stream(0x66, 0x77, 0xc2, 0x2d, 0x23, 0xf6, 0x00, 0x42, 0x9d, 0x64, 0x11, 0x5e, 0x9b, 0xfd, 0x4a, 0x08), enc()).succeeded);
    }

    private URI toURI(final String resource) throws URISyntaxException {
        return getClass().getResource(resource).toURI();
    }

}
