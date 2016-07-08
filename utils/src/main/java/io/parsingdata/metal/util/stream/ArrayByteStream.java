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

import java.io.IOException;

import io.parsingdata.metal.data.ByteStream;

/**
 * ByteStream implementation based on byte arrays.
 *
 * @author Netherlands Forensic Institute.
 */
public final class ArrayByteStream implements ByteStream {

    private final byte[] _buffer;

    /**
     * Construct a new ArrayByteStream wrapping an array of bytes.
     *
     * @param buffer the bytes to wrap
     */
    public ArrayByteStream(final byte[] buffer) {
        _buffer = buffer;
    }

    @Override
    public int read(final long offset, final byte[] buffer) throws IOException {
        if (offset + buffer.length > _buffer.length) {
            return 0;
        }
        System.arraycopy(_buffer, (int) offset, buffer, 0, buffer.length);
        return buffer.length;
    }
}