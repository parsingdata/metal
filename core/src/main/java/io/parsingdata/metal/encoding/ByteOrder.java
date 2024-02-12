/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

package io.parsingdata.metal.encoding;

public enum ByteOrder {

    BIG_ENDIAN { @Override public byte[] apply(final byte[] bytes) {
        return bytes.clone();
    } },
    LITTLE_ENDIAN { @Override public byte[] apply(final byte[] bytes) {
        final byte[] output = bytes.clone();
        for (int i = 0; i < bytes.length; i++) {
            output[i] = bytes[(bytes.length-1)-i];
        }
        return output;
    } };

    public abstract byte[] apply(final byte[] bytes);
}
