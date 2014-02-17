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

package nl.minvenj.nfi.ddrx.util;

import java.math.BigInteger;

import org.junit.Ignore;

import nl.minvenj.nfi.ddrx.expression.True;
import nl.minvenj.nfi.ddrx.expression.comparison.Equals;
import nl.minvenj.nfi.ddrx.expression.logical.Not;
import nl.minvenj.nfi.ddrx.expression.value.Con;
import nl.minvenj.nfi.ddrx.expression.value.Ref;
import nl.minvenj.nfi.ddrx.io.ByteStream;
import nl.minvenj.nfi.ddrx.token.Token;
import nl.minvenj.nfi.ddrx.token.Value;

@Ignore
public class TokenDefinitions {
    
    private TokenDefinitions() {}

    public static Token any(String name) {
        return new Value(name, new Con(BigInteger.valueOf(1)), new True());
    }

    public static Token fixed(String name, int value) {
        return new Value(name, new Con(BigInteger.valueOf(1)), new Equals(new Ref(name), new Con(BigInteger.valueOf(value))));
    }
    
    public static Token not(String name, int value) {
        return new Value(name, new Con(BigInteger.valueOf(1)), new Not(new Equals(new Ref(name), new Con(BigInteger.valueOf(value)))));
    }
    
    public static Token equalsRef(String name, String ref) {
        return new Value(name, new Con(BigInteger.valueOf(1)), new Equals(new Ref(name), new Ref(ref)));
    }
    
    public static ByteStream stream(int... bytes) {
        byte[] out = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            out[i] = (byte)bytes[i];
        }
        return new ByteStream(out);
    }

}
