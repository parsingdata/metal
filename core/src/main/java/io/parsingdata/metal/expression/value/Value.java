package io.parsingdata.metal.expression.value;

import java.math.BigInteger;
import java.util.BitSet;

import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.encoding.Encoding;

public interface Value {

    Slice getSlice();
    Encoding getEncoding();
    byte[] getValue();
    BigInteger getLength();
    BigInteger asNumeric();
    String asString();
    BitSet asBitSet();

}
