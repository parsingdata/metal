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

import static io.parsingdata.metal.Shorthand.toByteArray;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import io.parsingdata.metal.data.ParseState;

public class ParseStateFactory {

    public static ParseState stream(final int... bytes) {
        return new ParseState(new InMemoryByteStream(toByteArray(bytes)));
    }

    public static ParseState stream(final URI resource) throws IOException {
        return new ParseState(new InMemoryByteStream(Files.readAllBytes(Paths.get(resource))));
    }

    public static ParseState stream(final String value, final Charset charset) {
        return new ParseState(new InMemoryByteStream(value.getBytes(charset)));
    }

}
