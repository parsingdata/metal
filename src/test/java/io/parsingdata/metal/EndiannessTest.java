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

import static io.parsingdata.metal.Shorthand.and;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.self;
import static io.parsingdata.metal.Shorthand.shr;
import static io.parsingdata.metal.util.EncodingFactory.le;
import static io.parsingdata.metal.util.EncodingFactory.be;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

public class EndiannessTest {

    @Test
    public void andAcrossByteBoundaryLE() throws IOException {
        final Token t = def("x", con(2), eq(and(self, con(le(), 0xff, 0x03)), con(le(), 0x1b, 0x01)));
        Assert.assertTrue(t.parse(stream(0x1b, 0x81), le()).succeeded());
    }

    @Test
    public void constructIntermediateConstantLE() throws IOException {
        final Token t = def("x", con(2), eq(and(shr(con(le(), 0x82, 0x1b), con(1)), con(le(), 0x03, 0xff)), con(le(), 0x01, 0x0d)));
        Assert.assertTrue(t.parse(stream(0x00, 0x00), le()).succeeded());
    }

	
	@Test
	public void testLittleEndianAnd() {
		ValueExpression expression = and(con(0x1122), con(0x1100));
		Value val = expression.eval(null, le()).get();
		assertEquals(0x1100, val.asNumeric().intValue());
		
		assertEquals(ByteOrder.LITTLE_ENDIAN, val.getEncoding().getByteOrder());
		assertEquals("[0, 17]", Arrays.toString(val.getValue()));
	}
	
	@Test
	public void testBigEndianAnd() {
		ValueExpression expression = and(con(0x1122), con(0x1100));
		Value val = expression.eval(null, be()).get();
		assertEquals(0x1100, val.asNumeric().intValue());
		
		assertEquals(ByteOrder.BIG_ENDIAN, val.getEncoding().getByteOrder());
		assertEquals("[17, 0]", Arrays.toString(val.getValue()));
	}
	
	@Test
	public void testLittleEndianConstant() {
		ValueExpression expression = con(0x100, le());
		Value val = expression.eval(null, le()).get();
		assertEquals(0x100, val.asNumeric().intValue());
		
		assertEquals("[0, 1]", Arrays.toString(val.getValue()));
		assertEquals(ByteOrder.LITTLE_ENDIAN, val.getEncoding().getByteOrder());
	}
	
	@Test
	public void testBigEndianConstant() {
		ValueExpression expression = con(0x100, be());
		Value val = expression.eval(null, be()).get();
		assertEquals(0x100, val.asNumeric().intValue());
		
		assertEquals("[1, 0]", Arrays.toString(val.getValue()));
		assertEquals(ByteOrder.BIG_ENDIAN, val.getEncoding().getByteOrder());
	}
}
