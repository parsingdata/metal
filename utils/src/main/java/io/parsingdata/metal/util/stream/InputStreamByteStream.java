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

package io.parsingdata.metal.util.stream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.parsingdata.metal.data.ByteStream;

/**
 * ByteStream implementation based on InputStreams.
 *
 * @author Netherlands Forensic Institute.
 */
public final class InputStreamByteStream implements ByteStream, AutoCloseable {

    private static final int BUFFER_SIZE = 64 * 1024;

    private final BufferedInputStream _inputStream;
    private long _offset;

    /**
     * Construct a new InputStreamByteStream wrapping an InputStream.
     *
     * @param input the input stream to wrap
     */
    public InputStreamByteStream(final InputStream input) {
        _inputStream = new BufferedInputStream(input, BUFFER_SIZE);
        _inputStream.mark(Integer.MAX_VALUE);
    }

    @Override
    public int read(final long offset, final byte[] buffer) throws IOException {
        seek(offset);
        final int readBytes = _inputStream.read(buffer);

        _offset = offset + readBytes;
        return Math.max(0, readBytes);
    }

    private void seek(final long offset) throws IOException {
        if (offset < _offset) {
            _inputStream.reset();
            _inputStream.skip(offset);
        }
        else if (offset > _offset) {
            _inputStream.skip(offset - _offset);
        }
    }

    @Override
    public void close() {
        try {
            _inputStream.close();
        }
        catch (final IOException e) {
            throw new IllegalStateException("could not close stream", e);
        }
    }
}