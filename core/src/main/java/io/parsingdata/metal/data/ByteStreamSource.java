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

package io.parsingdata.metal.data;

import static io.parsingdata.metal.Util.checkNotNull;

import java.io.IOException;

public class ByteStreamSource extends Source {

    public final ByteStream input;

    public ByteStreamSource(final ByteStream input) {
        this.input = checkNotNull(input, "input");
    }

    @Override
    protected byte[] getData(final long offset, final int size) throws IOException {
        final byte[] data = new byte[size];
        final int readSize = input.read(offset, data);
        if (readSize == size) { return data; }
        final byte[] resizedData = new byte[readSize];
        System.arraycopy(data, 0, resizedData, 0, readSize);
        return resizedData;
    }

    @Override
    public String toString() {
        return input.toString();
    }

}
