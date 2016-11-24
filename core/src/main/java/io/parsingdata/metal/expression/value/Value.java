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

package io.parsingdata.metal.expression.value;

import static io.parsingdata.metal.Util.checkNotNull;

import java.io.IOException;
import java.math.BigInteger;
import java.util.BitSet;

import io.parsingdata.metal.Util;
import io.parsingdata.metal.data.Source;
import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.encoding.Sign;

public class Value {

    public final Source source;
    public final Encoding encoding;

    public Value(final Source source, final Encoding encoding) {
        this.source = source;
        this.encoding = checkNotNull(encoding, "encoding");
    }

    public byte[] getValue() throws IOException {
        return source.getData();
    }

    public BigInteger asNumeric() throws IOException {
        return encoding.sign == Sign.SIGNED ? new BigInteger(encoding.byteOrder.apply(getValue()))
                                            : new BigInteger(1, encoding.byteOrder.apply(getValue()));
    }

    public String asString() throws IOException {
        return new String(getValue(), encoding.charset);
    }

    public BitSet asBitSet() throws IOException {
        return BitSet.valueOf(encoding.byteOrder == ByteOrder.BIG_ENDIAN ? ByteOrder.LITTLE_ENDIAN.apply(getValue()) : getValue());
    }

    public OptionalValue operation(final ValueOperation operation) throws IOException {
        return operation.execute(this);
    }

    @Override
    public String toString() {
        try {
            return "0x" + Util.bytesToHexString(getValue());
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

}
