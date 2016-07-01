/**
 * Copyright 2016 National Cyber Security Centre, Netherlands Forensic Institute
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
package io.parsingdata.metal.util.stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class InputStreamByteStreamTest {

    @Rule
    public ExpectedException _thrown = ExpectedException.none();

    private InputStreamByteStream _byteStream;

    @Before
    public void setUp() {
        final byte[] bytes = {1, 2};
        _byteStream = new InputStreamByteStream(new ByteArrayInputStream(bytes));
    }

    @Test
    public void test() throws IOException {
        final byte[] readBytes = new byte[2];
        final int bytesRead = _byteStream.read(0, readBytes);

        assertThat(bytesRead, is(equalTo(2)));
        assertThat(readBytes, is(equalTo(new byte[]{1, 2})));
    }

    @Test
    public void testSingleBytes() throws IOException {
        final byte[] readBytes1 = new byte[1];
        final byte[] readBytes2 = new byte[1];
        final int bytesRead1 = _byteStream.read(0, readBytes1);
        final int bytesRead2 = _byteStream.read(1, readBytes2);

        assertThat(bytesRead1, is(equalTo(1)));
        assertThat(readBytes1, is(equalTo(new byte[]{1})));

        assertThat(bytesRead2, is(equalTo(1)));
        assertThat(readBytes2, is(equalTo(new byte[]{2})));
    }

    @Test
    public void testSingleBytesReversed() throws IOException {
        final byte[] readBytes1 = new byte[1];
        final byte[] readBytes2 = new byte[1];
        final int bytesRead2 = _byteStream.read(1, readBytes2);
        final int bytesRead1 = _byteStream.read(0, readBytes1);

        assertThat(bytesRead1, is(equalTo(1)));
        assertThat(readBytes1, is(equalTo(new byte[]{1})));

        assertThat(bytesRead2, is(equalTo(1)));
        assertThat(readBytes2, is(equalTo(new byte[]{2})));
    }

    @Test
    public void testReturnZero() throws IOException {
        final byte[] readBytes = new byte[1];
        final int bytesRead = _byteStream.read(2, readBytes);

        assertThat(bytesRead, is(equalTo(0)));
    }

    @Test
    public void testCloseInputStream() throws IOException {
        _thrown.expect(IOException.class);
        _thrown.expectMessage("Stream closed");

        _byteStream.close();

        final byte[] readBytes = new byte[1];
        _byteStream.read(0, readBytes);
    }
}
