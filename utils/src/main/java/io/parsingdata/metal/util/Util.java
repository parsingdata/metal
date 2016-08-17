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

package io.parsingdata.metal.util;

import java.io.IOException;

import io.parsingdata.metal.data.ByteStream;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.stream.ArrayByteStream;

/**
 * Utility class.
 *
 * @author Netherlands Forensic Institute.
 */
public final class Util {

    private Util() {
    }

    /**
     * Parses a {@link ByteStream} with given format.
     *
     * @param source the data to parse from
     * @param format the format to parse with
     * @return a {@link ParseResult}
     * @throws IOException see {@link Token#parse(Environment, Encoding)}
     */
    public static ParseResult parse(final ByteStream source, final Token format) throws IOException {
        return parse(source, 0, format);
    }

    /**
     * Parses a {@link ByteStream} from given offset, with given format.
     *
     * @param source the data to parse from
     * @param offset the offset to start parsing from
     * @param format the format to parse with
     * @return a {@link ParseResult}
     * @throws IOException see {@link Token#parse(Environment, Encoding)}
     */
    public static ParseResult parse(final ByteStream source, final long offset, final Token format) throws IOException {
        return parse(source, offset, format, new Encoding());
    }

    /**
     * Parses an array of bytes with given format.
     *
     * @param source the byte array to parse
     * @param format the format to parse with
     * @return a {@link ParseResult}
     * @throws IOException see {@link Token#parse(Environment, Encoding)}
     */
    public static ParseResult parse(final byte[] source, final Token format) throws IOException {
        return parse(new ArrayByteStream(source), 0L, format, new Encoding());
    }

    /**
     * Parses a {@link ByteStream} from given offset, with given format in given encoding.
     *
     * @param source the data to parse from
     * @param offset the offset to start parsing from
     * @param format the format to parse with
     * @param encoding the encoding to interpret the data in
     * @return a {@link ParseResult}
     * @throws IOException see {@link Token#parse(Environment, Encoding)}
     */
    public static ParseResult parse(final ByteStream source, final long offset, final Token format, final Encoding encoding) throws IOException {
        final Environment environment = new Environment(source, offset);
        return format.parse(environment, encoding);
    }

    /**
     * Convert varargs of tokens to an array of tokens.
     *
     * @param tokens the tokens to convert
     * @return an array containing the tokens
     */
    public static Token[] tokens(final Token... tokens) {
        return tokens;
    }
}