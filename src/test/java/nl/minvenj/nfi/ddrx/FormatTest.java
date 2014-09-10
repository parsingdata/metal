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

import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import nl.minvenj.nfi.ddrx.format.JPEG;
import nl.minvenj.nfi.ddrx.format.PNG;
import nl.minvenj.nfi.ddrx.format.ZIP;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class FormatTest {

    private static final String PNGFILE = "/test.png";
    private static final String ZIPFILE1 = "/singlefile-zip30-ubuntu.zip";
    private static final String ZIPFILE2 = "/multifile-zip30-ubuntu.zip";
    private static final String JPEGFILE = "/test.jpg";

    @Test
    public void parsePNG() throws IOException, URISyntaxException {
        Assert.assertTrue(PNG.FORMAT.parse(stream(toURI(PNGFILE)), enc()));
    }

    @Test
    public void parseZIP() throws IOException, URISyntaxException {
        Assert.assertTrue(ZIP.FORMAT.parse(stream(toURI(ZIPFILE1)), enc()));
    }

    @Test
    public void parseZIP2() throws IOException, URISyntaxException {
        Assert.assertTrue(ZIP.FORMAT.parse(stream(toURI(ZIPFILE2)), enc()));
    }

    @Test
    public void parseJPEG() throws IOException, URISyntaxException {
        Assert.assertTrue(JPEG.FORMAT.parse(stream(toURI(JPEGFILE)), enc()));
    }

    private URI toURI(String resource) throws URISyntaxException {
        return getClass().getResource(resource).toURI();
    }

}
