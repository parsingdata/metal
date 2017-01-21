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

package io.parsingdata.metal;

import static io.parsingdata.metal.data.ConstantSlice.create;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.UnaryValueExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

public final class Util {

    private Util() {}

    final private static char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray(); // Private because array content is mutable.

    public static <T>T checkNotNull(final T argument, final String name) {
        if (argument == null) { throw new IllegalArgumentException("Argument " + name + " may not be null."); }
        return argument;
    }

    public static <T>T[] checkContainsNoNulls(final T[] arguments, final String name) {
        checkNotNull(arguments, name);
        for (final T argument : arguments) {
            if (argument == null) { throw new IllegalArgumentException("Value in array " + name + " may not be null."); }
        }
        return arguments;
    }

    public static String tokensToString(final Token[] tokens) {
        checkNotNull(tokens, "tokens");
        return Arrays.stream(tokens).map(t -> t.toString()).reduce((l, r) -> l + ", " + r).orElse("");
    }

    public static String bytesToHexString(final byte[] bytes) {
        checkNotNull(bytes, "bytes");
        final char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            final int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static ValueExpression inflate(final ValueExpression target) {
        return new UnaryValueExpression(target) {
            @Override
            public Optional<Value> eval(final Value value, final Environment environment, final Encoding encoding) {
                final Inflater inf = new Inflater(true);
                inf.setInput(value.getValue());
                final byte[] dataReceiver = new byte[512];
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                while(!inf.finished()) {
                    try {
                        final int processed = inf.inflate(dataReceiver);
                        out.write(dataReceiver, 0, processed);
                    } catch (final DataFormatException e) {
                        return Optional.empty();
                    }
                }
                return Optional.of(new Value(create(out.toByteArray()), encoding));
            }
        };
    }

}
