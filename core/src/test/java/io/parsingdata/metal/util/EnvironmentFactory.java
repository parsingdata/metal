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

package io.parsingdata.metal.util;

import static io.parsingdata.metal.util.EncodingFactory.enc;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.callback.Callbacks;
import io.parsingdata.metal.encoding.Encoding;

public class EnvironmentFactory {

    public static Environment env(final String scope, final ParseState parseState, final Encoding encoding) {
        return new Environment(scope, parseState, encoding);
    }

    public static Environment env(final ParseState parseState, final Callbacks callbacks, final Encoding encoding) {
        return new Environment(parseState, callbacks, encoding);
    }

    public static Environment env(final ParseState parseState, final Encoding encoding) {
        return new Environment(parseState, encoding);
    }

    public static Environment env(final ParseState parseState) {
        return new Environment(parseState, enc());
    }

}
