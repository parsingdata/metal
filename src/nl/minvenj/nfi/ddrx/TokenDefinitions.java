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

package nl.minvenj.nfi.ddrx;

import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.def;
import static nl.minvenj.nfi.ddrx.Shorthand.expTrue;
import static nl.minvenj.nfi.ddrx.Shorthand.not;
import static nl.minvenj.nfi.ddrx.Shorthand.ref;
import nl.minvenj.nfi.ddrx.encoding.Encoding;
import nl.minvenj.nfi.ddrx.token.Token;

public class TokenDefinitions {

    private TokenDefinitions() {}

    public static Token any(String name) {
        return def(name, con(1));
    }

    public static Token any(String name, Encoding encoding) {
        return def(name, con(1), expTrue(), encoding);
    }

    public static Token eq(String name, int value) {
        return def(name, con(1), Shorthand.eq(con(value)));
    }

    public static Token notEq(String name, int value) {
        return def(name, con(1), not(Shorthand.eq(con(value))));
    }

    public static Token eqRef(String name, String ref) {
        return def(name, con(1), Shorthand.eq(ref(ref)));
    }

    public static Token notEqRef(String name, String ref) {
        return def(name, con(1), not(Shorthand.eq(ref(ref))));
    }

}
