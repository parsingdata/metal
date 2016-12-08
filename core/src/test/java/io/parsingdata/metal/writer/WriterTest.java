package io.parsingdata.metal.writer;

import static org.junit.Assert.assertArrayEquals;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import io.parsingdata.metal.token.Token;

public class WriterTest {

    @Test
    public void testWrite() throws Exception {
        final Token token = seq(
            def("length", 1),
            def("name", 7));

        final Data data = new Data();
        data._name = "Gertjan";
        data._length = (byte) data._name.length();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer.write(token, data, out, enc());

        final byte[] expecteds = new byte[]{7, 71, 101, 114, 116, 106, 97, 110};
        assertArrayEquals(expecteds, out.toByteArray());
    }

    public static class Data {

        @Name("name")
        public String _name;

        @Name("length")
        public byte _length;
    }
}
