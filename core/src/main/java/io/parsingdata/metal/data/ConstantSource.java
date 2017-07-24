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

import static io.parsingdata.metal.Util.bytesToHexString;
import static io.parsingdata.metal.Util.checkNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

import io.parsingdata.metal.Util;

public class ConstantSource extends Source {

    private final byte[] data; // Private because array content is mutable.

    public ConstantSource(final byte[] data) {
        this.data = checkNotNull(data, "data");
    }

    @Override
    protected byte[] getData(final long offset, final int size) throws IOException {
        if (offset >= data.length) { return new byte[0]; }
        final int toCopy = (int)offset + size > data.length ? data.length - (int)offset : size;
        final byte[] outputData = new byte[toCopy];
        System.arraycopy(data, (int)offset, outputData, 0, toCopy);
        return outputData;
    }

    @Override
    public String toString() {
        return bytesToHexString(data);
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Arrays.equals(data, ((ConstantSource)obj).data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass().hashCode(), Arrays.hashCode(data));
    }

}
