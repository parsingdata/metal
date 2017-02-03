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

package io.parsingdata.metal.format;

import static io.parsingdata.metal.data.ConstantSlice.create;

import java.util.Optional;
import java.util.zip.CRC32;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.UnaryValueExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

public final class Callback {

    private Callback() {}

    public static ValueExpression crc32(final ValueExpression target) {
        return new UnaryValueExpression(target) {
            @Override
            public Optional<Value> eval(final Value value, final ParseGraph graph, final Encoding encoding) {
                final CRC32 crc = new CRC32();
                crc.update(value.getValue());
                final long crcValue = crc.getValue();
                return Optional.of(new Value(create(encoding.byteOrder.apply(new byte[] { (byte)((crcValue & 0xff000000) >> 24),
                                                                                          (byte)((crcValue & 0xff0000) >> 16),
                                                                                          (byte)((crcValue & 0xff00) >> 8),
                                                                                          (byte) (crcValue & 0xff) })), encoding));
            }
        };
    }

}
