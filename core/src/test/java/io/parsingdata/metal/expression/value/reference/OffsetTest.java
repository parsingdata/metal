package io.parsingdata.metal.expression.value.reference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.offset;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Optional;

import org.junit.Test;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.expression.value.Value;

public class OffsetTest {

    @Test
    public void definedValueOffset() {
        final ImmutableList<Optional<Value>> offsetCon = offset(con(1)).eval(stream().order, enc());
        assertFalse(offsetCon.isEmpty());
        assertEquals(1, offsetCon.size);
        assertTrue(offsetCon.head.isPresent());
        assertEquals(0, offsetCon.head.get().asNumeric().intValueExact());
    }

}
