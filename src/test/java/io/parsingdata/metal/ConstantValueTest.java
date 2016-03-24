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
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

public class ConstantValueTest extends ParameterizedParse {

	@Parameters(name="{0} ({4})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        	{ "1 byte, Eq(0), Signed", single(1, eq(con(0))), stream(0), signed(), true },
        	{ "1 byte, Eq(0), Unsigned", single(1, eq(con(0))), stream(0), enc(), true },
        	{ "1 byte, Eq(-1), Signed", single(1, eq(con(-1))), stream(-1), signed(), true },
        	{ "1 byte, Eq(-1), Unsigned", single(1, eq(con(-1))), stream(-1), enc(), true },
        	{ "1 byte, EqNum(-1, Signed), Signed", single(1, eqNum(con(-1, signed()))), stream(-1), signed(), true },
        	{ "1 byte, EqNum(-1), Unsigned", single(1, eqNum(con(-1))), stream(-1), enc(), true },
        	{ "1 byte, Eq(0xff), Signed", single(1, eq(con(0xff))), stream(0xff), signed(), true },
        	{ "1 byte, Eq(0xff), Unsigned", single(1, eq(con(0xff))), stream(0xff), enc(), true },
        	{ "2 bytes, Eq(0x0123), Signed", single(2, eq(con(0x0123))), stream(0x01, 0x23), signed(), true },
        	{ "2 bytes, Eq(0x0123), Unsigned", single(2, eq(con(0x0123))), stream(0x01, 0x23), enc(), true },
        	{ "2 bytes, Eq(0x8123), Signed", single(2, eq(con(0x8123))), stream(0x81, 0x23), signed(), true },
        	{ "2 bytes, Eq(0x8123), Unsigned", single(2, eq(con(0x8123))), stream(0x81, 0x23), enc(), true },
        	{ "2 bytes, Eq(0x01, 0x23), Signed", single(2, eq(con(0x01, 0x23))), stream(0x01, 0x23), signed(), true },
        	{ "2 bytes, Eq(0x01, 0x23), Unsigned", single(2, eq(con(0x01, 0x23))), stream(0x01, 0x23), enc(), true },
        	{ "2 bytes, Eq(0x81, 0x23), Signed", single(2, eq(con(0x81, 0x23))), stream(0x81, 0x23), signed(), true },
        	{ "2 bytes, Eq(0x81, 0x23), Unsigned", single(2, eq(con(0x81, 0x23))), stream(0x81, 0x23), enc(), true },
        });
    }
    
    public ConstantValueTest(final String desc, final Token token, final Environment env, final Encoding enc, final boolean result) {
        super(token, env, enc, result);
    }
    
    private static Token single(int size, Expression predicate) {
    	return def("conValue", con(size), predicate);
    }

}
