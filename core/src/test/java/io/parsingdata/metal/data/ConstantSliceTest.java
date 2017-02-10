package io.parsingdata.metal.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Util.createFromBytes;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConstantSliceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void nullInput() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument data may not be null.");
        createFromBytes(null);
    }

    @Test
    public void checkData() {
        final byte[] input = { 1, 2, 3, 4 };
        final Slice slice = createFromBytes(input);
        final byte[] output = slice.getData();
        assertEquals(input.length, output.length);
        assertTrue(Arrays.equals(input, output));
    }

    @Test
    public void checkSource() throws IOException {
        final byte[] input = { 1, 2, 3, 4 };
        final Slice slice = createFromBytes(input);
        final byte[] output = slice.source.getData(0, 4);
        assertEquals(input.length, output.length);
        assertTrue(Arrays.equals(input, output));
        final byte[] outputBeyond = slice.source.getData(4, 1);
        assertEquals(0, outputBeyond.length);
        final byte[] outputPartial = slice.source.getData(2, 4);
        assertEquals(2, outputPartial.length);
        assertEquals(input[2], outputPartial[0]);
        assertEquals(input[3], outputPartial[1]);
    }

}
