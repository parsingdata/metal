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

import java.io.IOException;

import io.parsingdata.metal.expression.value.Value;

public class ValueByteStream implements ByteStream {

    public final Value value;

    public ValueByteStream(final Value value) {
        this.value = value;
    }

    @Override
    public int read(long offset, byte[] data) throws IOException {
        final byte[] inputData = value.getValue();
        if (offset >= inputData.length) { return 0; }
        final int toCopy = (int)offset + data.length > inputData.length ? inputData.length - (int)offset : data.length;
        System.arraycopy(inputData, (int)offset, data, 0, toCopy);
        return toCopy;
    }

}
