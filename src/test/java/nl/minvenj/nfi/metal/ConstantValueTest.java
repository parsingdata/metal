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

package nl.minvenj.nfi.metal;

import static nl.minvenj.nfi.metal.Shorthand.con;
import static nl.minvenj.nfi.metal.Shorthand.def;
import static nl.minvenj.nfi.metal.Shorthand.eq;
import static nl.minvenj.nfi.metal.Shorthand.eqNum;
import static nl.minvenj.nfi.metal.util.EncodingFactory.enc;
import static nl.minvenj.nfi.metal.util.EncodingFactory.signed;
import static nl.minvenj.nfi.metal.util.EnvironmentFactory.stream;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.expression.Expression;
import nl.minvenj.nfi.metal.token.Token;
import nl.minvenj.nfi.metal.util.ParameterizedParse;

public class ConstantValueTest extends ParameterizedParse {

	@Parameters(name="{0} ({4})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
//            { "[signed] 1 + 2 == 3", add, stream(1, 2, 3), signed(), true },
        	{ "a", single(1, eq(con(0))), stream(0), signed(), true },
        	{ "au", single(1, eq(con(0))), stream(0), enc(), true },
        	{ "a2", single(1, eq(con(-1))), stream(-1), signed(), true },
        	{ "a2u", single(1, eq(con(-1))), stream(-1), enc(), true },
        	{ "a3", single(1, eqNum(con(-1, signed()))), stream(-1), signed(), true },
        	{ "a3u", single(1, eqNum(con(-1))), stream(-1), enc(), true },
        	{ "b", single(1, eq(con(0xff))), stream(0xff), signed(), true },
        	{ "bu", single(1, eq(con(0xff))), stream(0xff), enc(), true },
        	{ "c", single(2, eq(con(0x0123))), stream(0x01, 0x23), signed(), true },
        	{ "cu", single(2, eq(con(0x0123))), stream(0x01, 0x23), enc(), true },
        	{ "d", single(2, eq(con(0x8123))), stream(0x81, 0x23), signed(), true },
        	{ "du", single(2, eq(con(0x8123))), stream(0x81, 0x23), enc(), true },
        	{ "e", single(2, eq(con(0x01, 0x23))), stream(0x01, 0x23), signed(), true },
        	{ "eu", single(2, eq(con(0x01, 0x23))), stream(0x01, 0x23), enc(), true },
        	{ "f", single(2, eq(con(0x81, 0x23))), stream(0x81, 0x23), signed(), true },
        	{ "fu", single(2, eq(con(0x81, 0x23))), stream(0x81, 0x23), enc(), true },
        });
    }
    
    public ConstantValueTest(final String desc, final Token token, final Environment env, final Encoding enc, final boolean result) {
        super(token, env, enc, result);
    }
    
    private static Token single(int size, Expression predicate) {
    	return def("conValue", con(size), predicate);
    }

}
