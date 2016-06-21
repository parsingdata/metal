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

package io.parsingdata.metal.util;

import java.io.IOException;

import io.parsingdata.metal.data.ByteStream;

public class InMemoryByteStream implements ByteStream {

    private final byte[] _data;

    public InMemoryByteStream(final byte[] data) {
        _data = data;
    }

    public int read(final long offset, final byte[] data) throws IOException {
        if (offset >= _data.length) { return 0; }
        final int toCopy = (int)offset + data.length > _data.length ? _data.length - (int)offset : data.length;
        System.arraycopy(_data, (int)offset, data, 0, toCopy);
        return toCopy;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _data.length + ")";
    }

}
