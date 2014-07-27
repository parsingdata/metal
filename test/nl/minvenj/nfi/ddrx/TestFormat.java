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
import java.nio.file.Path;
import java.nio.file.Paths;

import nl.minvenj.nfi.ddrx.format.JPEG;
import nl.minvenj.nfi.ddrx.format.PNG;
import nl.minvenj.nfi.ddrx.format.ZIP;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestFormat {

    private static final Path PNGFILE = Paths.get("testdata/test.png");
    private static final Path ZIPFILE1 = Paths.get("testdata/singlefile-zip30-ubuntu.zip");
    private static final Path ZIPFILE2 = Paths.get("testdata/multifile-zip30-ubuntu.zip");
    private static final Path JPEGFILE = Paths.get("testdata/test.jpg");

    @Test
    public void parsePNG() throws IOException {
        Assert.assertTrue(PNG.FORMAT.parse(stream(PNGFILE), enc()));
    }

    @Test
    public void parseZIP() throws IOException {
        Assert.assertTrue(ZIP.FORMAT.parse(stream(ZIPFILE1), enc()));
    }

    @Test
    public void parseZIP2() throws IOException {
        Assert.assertTrue(ZIP.FORMAT.parse(stream(ZIPFILE2), enc()));
    }
    
    @Test
    public void parseJPEG() throws IOException {
        Assert.assertTrue(JPEG.FORMAT.parse(stream(JPEGFILE), enc()));
    }

}
