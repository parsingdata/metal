package io.parsingdata.metal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.len;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;

public class ByteLengthTest {

    // Note that this token does not make sense,
    // but Len will become useful when Let is implemented
    private static final Token STRING = seq(
        def("length", 1),
        def("text1", ref("length")),
        def("text2", len(ref("text1"))));
    //  let("hasText", con(true), ltNum(len(ref("text1")), con(0))));

    @Test
    public void test() throws IOException {
        final byte[] text1 = string("Hello");
        final byte[] text2 = "Metal".getBytes(StandardCharsets.UTF_8);

        final ByteStream stream = new InMemoryByteStream(concat(text1, text2));
        final Environment env = new Environment(stream);
        final Encoding enc = new Encoding(false, StandardCharsets.UTF_8, ByteOrder.LITTLE_ENDIAN);
        final ParseResult result = STRING.parse(env, enc);

        assertTrue(result.succeeded());
        final ParseGraph graph = result.getEnvironment().order;
        assertEquals(5, graph.get("length").asNumeric().byteValue());
        assertEquals("Hello", graph.get("text1").asString());
        assertEquals("Metal", graph.get("text2").asString());
    }

    private byte[] string(final String text) {
        final byte[] data = text.getBytes(StandardCharsets.UTF_8);
        final byte[] length = {(byte) data.length};
        return concat(length, data);
    }

    private byte[] concat(final byte[] b1, final byte[] b2) {
        final byte[] result = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, result, 0, b1.length);
        System.arraycopy(b2, 0, result, b1.length, b2.length);
        return result;
    }
}
