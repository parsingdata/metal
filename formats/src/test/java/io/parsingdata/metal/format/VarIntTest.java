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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.format.VarInt.refVarInt;
import static io.parsingdata.metal.format.VarInt.varInt;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized;

import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

public class VarIntTest extends ParameterizedParse {

    @Parameterized.Parameters(name = "{0} ({4})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "[63, 63] 63 (varint) == 63", varIntAndValue(1), stream(63, 63), enc(), true },
            { "[127, 127] 127 (varint) == 127", varIntAndValue(1), stream(127, 127), enc(), true },
            { "[185, 10, 5, 57] (varint) == 1337", varIntAndValue(2), stream(185, 10, 5, 57), enc(), true },
            { "[160, 141, 6, 1, 134, 160] (varint) == 100000", varIntAndValue(3), stream(160, 141, 6, 1, 134, 160), enc(), true }
        });
    }

    public static final Token varIntAndValue(final int size) { return
        seq(varInt("vint_name"), post(def("full_name", con(size)), eqNum(refVarInt("vint_name"))));
    }

}
