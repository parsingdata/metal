/*
 * Copyright 2013-2021 Netherlands Forensic Institute
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

import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;
import static java.security.MessageDigest.getInstance;

import static io.parsingdata.metal.data.Slice.createFromBytes;
import static io.parsingdata.metal.data.Slice.createFromSource;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Optional;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.CoreValue;
import io.parsingdata.metal.expression.value.UnaryValueExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

public final class Util {

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray(); // Private because array content is mutable.

    private Util() {}

    public static <T>T checkNotNull(final T argument, final String name) {
        if (argument == null) {
            throw new IllegalArgumentException(format("Argument %s may not be null.", name));
        }
        return argument;
    }

    public static <T>T[] checkContainsNoNulls(final T[] arguments, final String name) {
        checkNotNull(arguments, name);
        for (final T argument : arguments) {
            if (argument == null) {
                throw new IllegalArgumentException(format("Value in array %s may not be null.", name));
            }
        }
        return arguments;
    }

    public static String checkNotEmpty(final String argument, final String name) {
        if (checkNotNull(argument, name).isEmpty()) {
            throw new IllegalArgumentException(format("Argument %s may not be empty.", name));
        }
        return argument;
    }

    public static boolean notNullAndSameClass(final Object object, final Object other) {
        return other != null
            && object.getClass() == other.getClass();
    }

    public static BigInteger checkNotNegative(final BigInteger argument, final String name) {
        if (checkNotNull(argument, name).compareTo(ZERO) < 0) {
            throw new IllegalArgumentException(format("Argument %s may not be negative.", name));
        }
        return argument;
    }

    public static String format(final String format, final Object... args) {
        return String.format(Locale.ENGLISH, format, args);
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
            public Optional<Value> eval(final Value value, final ParseState parseState, final Encoding encoding) {
                final Inflater inf = new Inflater(true);
                inf.setInput(value.value());
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
                return Optional.of(new CoreValue(Slice.createFromBytes(out.toByteArray()), encoding));
            }
        };
    }

    public static ValueExpression digest(final String algorithm, final ValueExpression target) {
        return new UnaryValueExpression(target) {
            @Override
            public Optional<Value> eval(final Value value, final ParseState parseState, final Encoding encoding) {
                try {
                    final Slice slice = value.slice();
                    final BigInteger bufferSize = valueOf(512);
                    final MessageDigest digester = getInstance(algorithm);

                    for (BigInteger i = ZERO; i.compareTo(slice.length) < 0; i = i.add(bufferSize)) {
                        final BigInteger length = bufferSize.min(slice.length.subtract(i));
                        final Optional<Slice> subslice = createFromSource(slice.source, slice.offset.add(i), length);
                        if (subslice.isEmpty()) {
                            return Optional.empty();
                        }
                        digester.update(subslice.get().getData());
                    }

                    return Optional.of(new CoreValue(createFromBytes(encoding.byteOrder.apply(digester.digest())), encoding));
                }
                catch (final NoSuchAlgorithmException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        };
    }

    public static Optional<ParseState> success(final ParseState parseState) {
        return Optional.of(parseState);
    }

    public static Optional<ParseState> failure() {
        return Optional.empty();
    }

}
