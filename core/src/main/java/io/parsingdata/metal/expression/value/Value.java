/*
 * Copyright 2013-2021 Netherlands Forensic Institute
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

import java.math.BigInteger;
import java.util.BitSet;

import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.encoding.Encoding;

public interface Value {

    Slice slice();

    Encoding encoding();

    byte[] value();

    BigInteger length();

    BigInteger asNumeric();

    String asString();

    BitSet asBitSet();

}
