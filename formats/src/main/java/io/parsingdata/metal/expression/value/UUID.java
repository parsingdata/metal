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

import static io.parsingdata.metal.Shorthand.cat;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.encoding.Encoding.DEFAULT_ENCODING;

import java.math.BigInteger;

import io.parsingdata.metal.encoding.Encoding;

/**
 * {@link UUID#uuid(String)} creates a ValueExpression to be used as predicate for 16 byte definitions;
 */
public final class UUID {

    private static final Encoding BIG_ENDIAN = DEFAULT_ENCODING;

    private UUID() {}

    /**
     * Use a String representation of a UUID as predicate.
     * {@code eq(uuid("caa16737-fa36-4d43-b3b6-33f0aa44e76b"))}
     * @param uuid UUID, for example "c79577f6-2ff6-4b48-a252-1c88d4416cd8"
     * @return expression to use as predicate
     */
    public static ValueExpression uuid(final String uuid) {
        final java.util.UUID value = java.util.UUID.fromString(checkNotNull(uuid, "uuid"));
        return cat(exactLong(value.getMostSignificantBits()), exactLong(value.getLeastSignificantBits()));
    }

    /**
     * Create {@link ValueExpression} without compacting the bytes.
     * @param bits Long to convert to {@link ValueExpression}
     * @return {@link ValueExpression} of the bits
     */
    private static ValueExpression exactLong(final long bits) {
        return con(ConstantFactory.createFromBytes(BigInteger.valueOf(bits).toByteArray(), BIG_ENDIAN));
    }
}
