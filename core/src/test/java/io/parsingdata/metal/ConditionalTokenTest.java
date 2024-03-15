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

package io.parsingdata.metal;

import static io.parsingdata.metal.Shorthand.CURRENT_OFFSET;
import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ltNum;
import static io.parsingdata.metal.Shorthand.offset;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.whl;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.Collection;
import java.util.List;

import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

public class ConditionalTokenTest extends ParameterizedParse {

    private static final Token preToken = seq(any("a"),
        opt(pre(any("b"), eqNum(ref("a"), con(1)))),
        def("c", con(1), eqNum(con(3))));

    private static final Token whileToken = seq(any("size"),
        whl(any("value"), ltNum(CURRENT_OFFSET, add(ref("size"), add(offset(last(ref("size"))), con(1))))),
        def("footer", con(1), eq(con(0xff))));

    @Override
    public Collection<Object[]> data() {
        return List.of(new Object[][] {
            { "[1, 2, 3] a b c", preToken, stream(1, 2, 3), enc(), true },
            { "[2, 3] a c", preToken, stream(2, 3), enc(), true },
            { "[1, 2, 2] a b c(error)", preToken, stream(1, 2, 2), enc(), false },
            { "[2, 2] a c(error)", preToken, stream(2, 2), enc(), false },
            { "[2, 1, 3, -1] a (a x any) -1", whileToken, stream(2, 1, 3, -1), enc(), true },
            { "[0, -1] a (a x any) -1", whileToken, stream(0, -1), enc(), true },
            { "[2, -1, -1, 0, -1, -1] a (a x any) -1(error)", whileToken, stream(2, -1, -1, 0, -1, -1), enc(), false },
            { "[0, 0, -1] a (a x any) -1(error)", whileToken, stream(0, 0, -1), enc(), false }
        });
    }

}
