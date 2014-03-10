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
import static nl.minvenj.nfi.ddrx.Shorthand.defNum;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.expTrue;
import static nl.minvenj.nfi.ddrx.Shorthand.not;
import static nl.minvenj.nfi.ddrx.Shorthand.refNum;

import nl.minvenj.nfi.ddrx.token.Token;

public class TokenDefinitions {
    
    private TokenDefinitions() {}

    public static Token anyNum(String name) {
        return defNum(name, con(1), expTrue());
    }

    public static Token eqNum(String name, int value) {
        return defNum(name, con(1), eq(con(value)));
    }
    
    public static Token notEqNum(String name, int value) {
        return defNum(name, con(1), not(eq(con(value))));
    }
    
    public static Token eqRefNum(String name, String ref) {
        return defNum(name, con(1), eq(refNum(ref)));
    }
    
    public static Token notEqRefNum(String name, String ref) {
        return defNum(name, con(1), not(eq(refNum(ref))));
    }
    
}
