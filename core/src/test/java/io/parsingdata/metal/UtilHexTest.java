package io.parsingdata.metal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

@RunWith(Parameterized.class)
public class UtilHexTest {

    @Parameter(0)
    public byte[] input;

    @Parameter(1)
    public String output;

    @Parameterized.Parameters(name = "{1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { new byte[] { 0x01, 0x23, 0x45, 0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef}, "0123456789ABCDEF" },
                { new byte[] { -1 }, "FF" },
                { new byte[0], "" }
        });
    }

    @Test
    public void byteToHex() {
        assertThat(Util.bytesToHexString(input), is(equalTo(output)));
    }

}
