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

package io.parsingdata.metal.expression.value;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.nio.ByteBuffer.allocate;

import static io.parsingdata.metal.Shorthand.cat;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.format;
import static io.parsingdata.metal.encoding.Encoding.DEFAULT_ENCODING;

import java.util.Arrays;

import io.parsingdata.metal.encoding.Encoding;

/**
 * {@link GUID#guid(String)} creates a ValueExpression to be used as predicate for 16 byte definitions;
 */
public final class GUID {

    private static final Encoding BIG_ENDIAN = DEFAULT_ENCODING;

    private GUID() {}

    /**
     * Use a String representation of a GUID as predicate.
     * {@code eq(guid("caa16737-fa36-4d43-b3b6-33f0aa44e76b"))}
     * Note that the byte order in the encoding matters for the output.
     * @param guid GUID, for example "caa16737-fa36-4d43-b3b6-33f0aa44e76b"
     * @return expression to use as predicate
     */
    public static ValueExpression guid(final String guid) {
        final String[] parts = checkNotNull(guid, "guid").split("-", -1);
        if (parts.length != 5) {
            throw new IllegalArgumentException(format("Invalid GUID string: %s", guid));
        }
        return (parseState, encoding) ->
            // Note that GUID bytes differ from UUID bytes, as the first 3 parts can be reversed
            cat(
                cat(
                    cat(
                        four(parts[0], encoding),
                        two(parts[1], encoding)),
                    two(parts[2], encoding)),
                cat(
                    two(parts[3], BIG_ENDIAN),
                    six(parts[4], BIG_ENDIAN))).eval(parseState, encoding);
    }

    private static ValueExpression four(final String part, final Encoding encoding) {
        return encode(allocate(4)
            .putInt(0, (int) parseLong(part, 16))
            .array(), encoding);
    }

    private static ValueExpression two(final String part, final Encoding encoding) {
        return encode(allocate(2)
            .putShort(0, (short) parseInt(part, 16))
            .array(), encoding);
    }

    private static ValueExpression six(final String part, final Encoding encoding) {
        return encode(Arrays.copyOfRange(
            allocate(8)
                .putLong(0, parseLong(part, 16))
                .array(),
            2, 8), encoding);
    }

    private static ValueExpression encode(final byte[] bytes, final Encoding encoding) {
        return con(ConstantFactory.createFromBytes(encoding.byteOrder.apply(bytes), encoding));
    }
}
