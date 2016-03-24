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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Inflater;
import java.util.zip.InflaterOutputStream;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.UnaryValueExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.expression.value.ValueOperation;

public class Callback {

    public static ValueExpression crc32(final ValueExpression target) {
        return new UnaryValueExpression(target) {
            @Override
            public OptionalValue eval(final Value v, final Environment env, final Encoding enc) {
                return v.operation(new ValueOperation() {
                    @Override
                    public OptionalValue execute(final Value value) {
                        final CRC32 crc = new CRC32();
                        crc.update(value.getValue());
                        final long crcValue = crc.getValue();
                        return OptionalValue.of(new Value(enc.getByteOrder().apply(new byte[] { (byte)((crcValue & 0xff000000) >> 24),
                                                                                                (byte)((crcValue & 0xff0000) >> 16),
                                                                                                (byte)((crcValue & 0xff00) >> 8),
                                                                                                (byte)(crcValue & 0xff) }), enc));
                    }
                });
            }
        };
    }

    public static ValueExpression inflate(final ValueExpression target) {
        return new UnaryValueExpression(target) {
            @Override
            public OptionalValue eval(final Value v, final Environment env, final Encoding enc) {
                return v.operation(new ValueOperation() {
                    @Override
                    public OptionalValue execute(final Value value) {
                        final Inflater inf = new Inflater(true);
                        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        final InflaterOutputStream ios = new InflaterOutputStream(bos, inf);
                        try {
                            ios.write(value.getValue());
                            ios.close();
                            return OptionalValue.of(new Value(bos.toByteArray(), enc));
                        } catch (final IOException e) {
                            return OptionalValue.empty();
                        }
                    }
                });
            }
        };
    }

}
