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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.expTrue;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.ref;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;

public class TokenDefinitions {

    private TokenDefinitions() {}

    public static Token any(final String name) {
        return def(name, con(1));
    }

    public static Token any(final String name, final Encoding encoding) {
        return def(name, con(1), expTrue(), encoding);
    }

    public static Token eq(final String name, final int value) {
        return def(name, con(1), Shorthand.eq(con(value)));
    }

    public static Token notEq(final String name, final int value) {
        return def(name, con(1), not(Shorthand.eq(con(value))));
    }

    public static Token eqRef(final String name, final String ref) {
        return def(name, con(1), Shorthand.eq(ref(ref)));
    }

    public static Token notEqRef(final String name, final String ref) {
        return def(name, con(1), not(Shorthand.eq(ref(ref))));
    }

}
