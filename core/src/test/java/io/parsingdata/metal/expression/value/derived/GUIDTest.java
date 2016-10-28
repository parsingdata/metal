package io.parsingdata.metal.expression.value.derived;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.guid;
import static io.parsingdata.metal.Shorthand.uuid;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.le;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.token.Token;

public class GUIDTest {

    @Rule
    public ExpectedException _exception = ExpectedException.none();

    @Test
    public void parseGUID() throws IOException, URISyntaxException {
        final Token guid = def("guid", 16, eq(guid("00c27766-f623-4200-9d64-115e9bfd4a08")));
        assertTrue("be", guid.parse(stream(0x00, 0xc2, 0x77, 0x66, 0xf6, 0x23, 0x42, 0x00, 0x9d, 0x64, 0x11, 0x5e, 0x9b, 0xfd, 0x4a, 0x08), enc()).succeeded);
        assertTrue("le", guid.parse(stream(0x66, 0x77, 0xc2, 0x00, 0x23, 0xf6, 0x00, 0x42, 0x9d, 0x64, 0x11, 0x5e, 0x9b, 0xfd, 0x4a, 0x08), le()).succeeded);

        _exception.expect(IllegalArgumentException.class);
        _exception.expectMessage("Invalid GUID string: test");
        guid("test");
    }

    @Test
    public void parseUUID() throws IOException, URISyntaxException {
        final Token uuid = def("uuid", 16, eq(uuid("00c27766-f623-4200-9d64-115e9bfd4a08")));
        assertTrue(uuid.parse(stream(0x00, 0xc2, 0x77, 0x66, 0xf6, 0x23, 0x42, 0x00, 0x9d, 0x64, 0x11, 0x5e, 0x9b, 0xfd, 0x4a, 0x08), enc()).succeeded);
        assertFalse(uuid.parse(stream(0x66, 0x77, 0xc2, 0x00, 0x23, 0xf6, 0x00, 0x42, 0x9d, 0x64, 0x11, 0x5e, 0x9b, 0xfd, 0x4a, 0x08), enc()).succeeded);

        _exception.expect(IllegalArgumentException.class);
        _exception.expectMessage("Invalid UUID string: test");
        uuid("test");
    }
}
