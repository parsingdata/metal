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

import static io.parsingdata.metal.Util.checkNotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import io.parsingdata.metal.ImmutableObject;
import io.parsingdata.metal.Util;

public class Encoding extends ImmutableObject {

    public static final Sign DEFAULT_SIGN = Sign.UNSIGNED;
    public static final Charset DEFAULT_CHARSET = StandardCharsets.US_ASCII;
    public static final ByteOrder DEFAULT_BYTE_ORDER = ByteOrder.BIG_ENDIAN;

    public static final Encoding DEFAULT_ENCODING = new Encoding(DEFAULT_SIGN, DEFAULT_CHARSET, DEFAULT_BYTE_ORDER);

    public final Sign sign;
    public final Charset charset;
    public final ByteOrder byteOrder;

    public Encoding(final Sign signed) {
        this(signed, DEFAULT_CHARSET, DEFAULT_BYTE_ORDER);
    }

    public Encoding(final Charset charset) {
        this(DEFAULT_SIGN, charset, DEFAULT_BYTE_ORDER);
    }

    public Encoding(final ByteOrder byteOrder) {
        this(DEFAULT_SIGN, DEFAULT_CHARSET, byteOrder);
    }

    public Encoding(final Sign sign, final Charset charset, final ByteOrder byteOrder) {
        this.sign = checkNotNull(sign, "sign");
        this.charset = checkNotNull(charset, "charset");
        this.byteOrder = checkNotNull(byteOrder, "byteOrder");
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + sign + "," + charset + "," + byteOrder + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return Util.notNullAndSameClass(this, obj)
            && Objects.equals(sign, ((Encoding) obj).sign)
            && Objects.equals(charset, ((Encoding) obj).charset)
            && Objects.equals(byteOrder, ((Encoding) obj).byteOrder);
    }

    @Override
    public int immutableHashCode() {
        return Objects.hash(getClass(), sign, charset, byteOrder);
    }

}
