/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.len;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.format.VarInt.decodeVarInt;
import static io.parsingdata.metal.format.VarInt.varInt;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Collection;
import java.util.List;

import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

public class VarIntTest extends ParameterizedParse {

    public static Token varIntAndValue(final int size) {
        return
            seq(varInt("vint_name"), post(def("full_name", con(size)), eqNum(decodeVarInt(last(ref("vint_name"))))));
    }

    public static final Token REPN_AUTO_SIZE_VARINT =
        repn(
            seq(
                varInt("varInt"),
                post(def("decoded", len(decodeVarInt(last(ref("varInt"))))), eq(decodeVarInt(last(ref("varInt")))))
            ), con(4));

    @Override
    public Collection<Object[]> data() {
        return List.of(new Object[][] {
            { "[63, 63] 63 (varint) == 63", varIntAndValue(1), stream(63, 63), enc(), true },
            { "[127, 127] 127 (varint) == 127", varIntAndValue(1), stream(127, 127), enc(), true },
            { "[185, 10, 5, 57] (varint) == 1337", varIntAndValue(2), stream(185, 10, 5, 57), enc(), true },
            { "[160, 141, 6, 1, 134, 160] (varint) == 100000", varIntAndValue(3), stream(160, 141, 6, 1, 134, 160), enc(), true },
            { "[63, 63, 185, 10, 5, 57, 127, 127, 160, 141, 6, 1, 134, 160] 4x(varint) == 4x(decoded)", REPN_AUTO_SIZE_VARINT, stream(63, 63, 185, 10, 5, 57, 127, 127, 160, 141, 6, 1, 134, 160), enc(), true }
        });
    }

}
