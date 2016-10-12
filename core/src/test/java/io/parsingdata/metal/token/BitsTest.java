package io.parsingdata.metal.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.bit;
import static io.parsingdata.metal.Shorthand.bits;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.expTrue;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.selection.ByName;
import io.parsingdata.metal.util.InMemoryByteStream;

public class BitsTest {

    @Test
    public void testBits() throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES).putLong(0x400006);
        final Environment env = new Environment(new InMemoryByteStream(buffer.array()));

        final Token bits = bits("entry", con(8),
            bit("State", con(3), expTrue()),
            bit("Reserved", con(17), eqNum(con(0))),
            bit("Offset", con(44), expTrue()));

        final ParseResult result = bits.parse(env, enc());
        assertTrue(result.succeeded);
        final ParseGraph graph = result.environment.order;

        assertEquals(6, ByName.getValue(graph, "State").asNumeric().longValue());
        assertEquals(4, ByName.getValue(graph, "Offset").asNumeric().intValue());
    }
}
