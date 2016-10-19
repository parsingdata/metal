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

import static io.parsingdata.metal.Shorthand.cat;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.encoding.ByteOrder.LITTLE_ENDIAN;

import java.nio.ByteBuffer;
import java.util.UUID;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.value.Value;

/**
 * Predicates for Microsoft GUID and Java UUID.
 *
 * @author Netherlands Forensic Institute.
 */
public class UID {
    /**
     * Use a String representation of a GUID as predicate.
     * @param guid GUID, for example "caa16737-fa36-4d43-b3b6-33f0aa44e76b"
     * @return expression to use as predicate
     */
    public static Expression guid(final String guid) {
        final String[] parts = guid.split("-");
        if (parts.length != 5) {
            throw new IllegalArgumentException("Invalid GUID string: " + guid);
        }

        // Note that UID bytes differ from UUID bytes, as the first 3 parts are reversed
        // Use ByteBuffer instead of long to make sure no leading zeroes are omitted
        final ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putInt(0, Integer.reverseBytes((int) Long.parseLong(parts[0], 16)));
        buffer.putShort(4, Short.reverseBytes((short) Integer.parseInt(parts[1], 16)));
        buffer.putShort(6, Short.reverseBytes((short) Integer.parseInt(parts[2], 16)));
        buffer.putLong(8, Long.parseLong(parts[4], 16));
        buffer.putShort(8, (short) Integer.parseInt(parts[3], 16));

        return eq(con(new Value(buffer.array(), new Encoding(LITTLE_ENDIAN))));
    }

    /**
     * Use a String representation of a UUID as predicate.
     * @param guid UUID, for example "c79577f6-2ff6-4b48-a252-1c88d4416cd8"
     * @return expression to use as predicate
     */
    public static Expression uuid(final String uuid) {
        final UUID value = UUID.fromString(uuid);
        return eq(cat(con(value.getMostSignificantBits()), con(value.getLeastSignificantBits())));
    }
}
