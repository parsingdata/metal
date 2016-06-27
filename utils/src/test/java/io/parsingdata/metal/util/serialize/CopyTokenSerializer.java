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
package io.parsingdata.metal.util.serialize;

import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.util.serialize.process.ParseValueProcessor;

/**
 * Used to copy value bytes to a byte array at the value's original offset.
 *
 * Values not contained in the parse result but which were present
 * in the original data are not copied, i.e. they all have a value of 0.
 *
 * @author Netherlands Forensic Institute.
 */
public final class CopyTokenSerializer implements ParseValueProcessor {

    private final byte[] _bytes;

    public CopyTokenSerializer(final int length) {
        _bytes = new byte[length];
    }

    @Override
    public void process(final ParseValue value) {
        final byte[] bytes = value.getValue();
        System.arraycopy(bytes, 0, _bytes, (int) value.getOffset(), bytes.length);
    }

    public byte[] outputData() {
        return _bytes;
    }
}