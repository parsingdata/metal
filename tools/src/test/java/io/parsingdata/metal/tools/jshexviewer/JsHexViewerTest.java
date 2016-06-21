package io.parsingdata.metal.tools.jshexviewer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.le;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.EnvironmentFactory;

/**
 * Generate a HTML page to view the Metal ParseGraph in a hex viewer.
 *
 * @author Netherlands Forensic Institute.
 */
public class JsHexViewerTest {

    private static final Token STRING = seq(
        def("length", 1),
        def("text", ref("length")));

    @Test
    public void testGenerate() throws Exception {
        final Environment env = EnvironmentFactory.stream(7, 'G', 'e', 'r', 't', 'j', 'a', 'n');
        final ParseResult result = STRING.parse(env, le());

        assertTrue(result.succeeded());

        // Write the data so it can be loaded manually in the viewer
        final File root = new File(getClass().getResource("/jsHexViewer").toURI());
        try (FileOutputStream out = new FileOutputStream(new File(root, "data"))) {
            final byte[] buffer = new byte[8];
            env.input.read(0, buffer);
            out.write(buffer);
        }

        JsHexViewer.generate(result.getEnvironment().order);

        final String generated = IOUtils.toString(getClass().getResourceAsStream("/jsHexViewer.htm"));
        final String expected = IOUtils.toString(getClass().getResourceAsStream("/jsHexViewer/jsHexViewer_data.htm"));
        assertEquals(expected, generated);
    }
}
