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

public class Encoding {

    private static final Sign DEFAULT_SIGNED = Sign.UNSIGNED;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.US_ASCII;
    private static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.BIG_ENDIAN;

    private final Sign sign;
    private final Charset charset;
    private final ByteOrder byteOrder;

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

    public Sign getSign() {
        return sign;
    }

    public boolean isSigned() {
        return sign == Sign.SIGNED;
    }

    public Charset getCharset() {
        return charset;
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sign + "," + charset + "," + byteOrder + ")";
    }
}
