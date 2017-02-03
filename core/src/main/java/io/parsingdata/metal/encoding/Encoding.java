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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import io.parsingdata.metal.data.Slice;

public class Encoding {

    public static final Sign DEFAULT_SIGNED = Sign.UNSIGNED;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.US_ASCII;
    public static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.BIG_ENDIAN;

    public final Sign sign;
    public final Charset charset;
    public final ByteOrder byteOrder;

    public Encoding() {
        this(DEFAULT_SIGNED, DEFAULT_CHARSET, DEFAULT_BYTE_ORDER);
    }

    public Encoding(final Sign signed) {
        this(signed, DEFAULT_CHARSET, DEFAULT_BYTE_ORDER);
    }

    public Encoding(final Charset charset) {
        this(DEFAULT_SIGNED, charset, DEFAULT_BYTE_ORDER);
    }

    public Encoding(final ByteOrder byteOrder) {
        this(DEFAULT_SIGNED, DEFAULT_CHARSET, byteOrder);
    }

    public Encoding(final Sign sign, final Charset charset, final ByteOrder byteOrder) {
        this.sign = sign;
        this.charset = charset;
        this.byteOrder = byteOrder;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sign + "," + charset + "," + byteOrder + ")";
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null
            && getClass() == obj.getClass()
            && Objects.equals(sign, ((Encoding)obj).sign)
            && Objects.equals(charset, ((Encoding)obj).charset)
            && Objects.equals(byteOrder, ((Encoding)obj).byteOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sign, charset, byteOrder);
    }

}
