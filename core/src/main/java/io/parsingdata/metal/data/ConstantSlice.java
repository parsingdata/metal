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

public class ConstantSlice extends Slice {

    public ConstantSlice(byte[] data) {
        super(new ConstantSource(checkNotNull(data, "data")), 0, data.length);
    }

    private static class ConstantSource extends Source {

        private final byte[] data;

        public ConstantSource(byte[] data) {
            this.data = data;
        }

        @Override
        public byte[] getData(long offset, int size) throws IOException {
            if (offset >= data.length) { return new byte[0]; }
            final int toCopy = (int)offset + size > data.length ? data.length - (int)offset : size;
            final byte[] outputData = new byte[toCopy];
            System.arraycopy(data, (int)offset, outputData, 0, toCopy);
            return outputData;
        }

        @Override
        public String toString() {
            return data.toString();
        }
    }
}
