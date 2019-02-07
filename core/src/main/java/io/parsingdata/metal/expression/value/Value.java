package io.parsingdata.metal.expression.value;

import static io.parsingdata.metal.encoding.Encoding.DEFAULT_ENCODING;

import java.math.BigInteger;
import java.util.BitSet;

import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.encoding.Encoding;

public interface Value {

    Value NOT_A_VALUE = new CoreValue(Slice.createFromBytes(new byte[]{}), DEFAULT_ENCODING);

    Slice getSlice();
    Encoding getEncoding();
    byte[] getValue();
    BigInteger getLength();
    BigInteger asNumeric();
    String asString();
    BitSet asBitSet();

}
