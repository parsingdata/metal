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

package io.parsingdata.metal.encoding;

public enum ByteOrder {

    BIG_ENDIAN { public byte[] apply(final byte[] b) { return b.clone(); } },
    LITTLE_ENDIAN { public byte[] apply(final byte[] b) {
        final byte[] o = b.clone();
        for (int i = 0; i < b.length; i++) {
            o[i] = b[(b.length-1)-i];
        }
        return o;
    } };

    public abstract byte[] apply(final byte[] b);
}
