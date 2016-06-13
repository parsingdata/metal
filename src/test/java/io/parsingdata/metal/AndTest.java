package io.parsingdata.metal;


import static io.parsingdata.metal.Shorthand.*;
import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;

import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

public class AndTest {
	
	@Test
	public void testLittleEndian() {
		Encoding enc = new Encoding(false, StandardCharsets.UTF_8, ByteOrder.LITTLE_ENDIAN);
		
		ValueExpression expression = and(con(0x1122), con(0x1100));
		Value val = expression.eval(null, enc).get();
		assertEquals(0x1100, val.asNumeric().intValue());
		
		assertEquals("[0, 17]", Arrays.toString(val.getValue()));
		assertEquals(ByteOrder.LITTLE_ENDIAN, val.getEncoding().getByteOrder());
	}
	
	
	@Test
	public void testBigEndian() {
		Encoding enc = new Encoding(false, StandardCharsets.UTF_8, ByteOrder.BIG_ENDIAN);
		
		ValueExpression expression = and(con(0x1122), con(0x1100));
		Value val = expression.eval(null, enc).get();
		assertEquals(0x1100, val.asNumeric().intValue());
		
		assertEquals("[17, 0]", Arrays.toString(val.getValue()));
		assertEquals(ByteOrder.BIG_ENDIAN, val.getEncoding().getByteOrder());
	}
}
