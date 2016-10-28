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

package io.parsingdata.metal.format;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.nio.ByteBuffer.allocate;

import static io.parsingdata.metal.Shorthand.cat;
import static io.parsingdata.metal.Shorthand.con;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.UUID;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * Constants for Microsoft GUID and Java UUID.
 *
 * @author Netherlands Forensic Institute.
 */
public class UID {
    private static final Encoding BIG_ENDIAN = new Encoding();

    /**
     * Use a String representation of a GUID as predicate.
     * {@code eq(guid("caa16737-fa36-4d43-b3b6-33f0aa44e76b"))}
     * Note that the byte order in the encoding matters for the output.
     * @param guid GUID, for example "caa16737-fa36-4d43-b3b6-33f0aa44e76b"
     * @return expression to use as predicate
     */
    public static ValueExpression guid(final String guid) {
        final String[] parts = guid.split("-");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid GUID string: " + guid);
        }
        return new ValueExpression() {

            @Override
            public OptionalValueList eval(final Environment environment, final Encoding encoding) {
                // Note that GUID bytes differ from UUID bytes, as the first 3 parts can be reversed
                return cat(
                    cat(
                        cat(
                            in(parts[0], encoding),
                            sh(parts[1], encoding)),
                        sh(parts[2], encoding)),
                    cat(
                        sh(parts[3], BIG_ENDIAN),
                        ln(parts[4], BIG_ENDIAN))).eval(environment, encoding);
            }
        };
    }

    private static ValueExpression in(final String part, final Encoding encoding) {
        return encode(allocate(4)
            .putInt(0, (int) parseLong(part, 16))
            .array(), encoding);
    }

    private static ValueExpression sh(final String part, final Encoding encoding) {
        return encode(allocate(2)
            .putShort(0, (short) parseInt(part, 16))
            .array(), encoding);
    }

    private static ValueExpression ln(final String part, final Encoding encoding) {
        return encode(Arrays.copyOfRange(
            allocate(8)
                .putLong(0, Long.parseLong(part, 16))
                .array(),
            2, 8), encoding);
    }

    private static ValueExpression encode(final byte[] bytes, final Encoding encoding) {
        return con(ConstantFactory.createFromBytes(encoding.byteOrder.apply(bytes), encoding));
    }

    /**
     * Use a String representation of a UUID as predicate.
     * {@code eq(uuid("caa16737-fa36-4d43-b3b6-33f0aa44e76b"))}
     * @param uuid UUID, for example "c79577f6-2ff6-4b48-a252-1c88d4416cd8"
     * @return expression to use as predicate
     */
    public static ValueExpression uuid(final String uuid) {
        final UUID value = UUID.fromString(uuid);
        return cat(exactLong(value.getMostSignificantBits()), exactLong(value.getLeastSignificantBits()));
    }

    /**
     * Create {@link ValueExpression} without compacting the bytes.
     * @param bits Long to convert to {@link ValueExpression}
     * @return {@link ValueExpression} of the bits
     */
    public static ValueExpression exactLong(final long bits) {
        return con(ConstantFactory.createFromBytes(BigInteger.valueOf(bits).toByteArray(), BIG_ENDIAN));
    }
}
