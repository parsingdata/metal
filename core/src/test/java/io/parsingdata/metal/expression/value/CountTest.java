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

package io.parsingdata.metal.expression.value;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.count;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Collection;
import java.util.List;

import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

public class CountTest extends ParameterizedParse {

    private static final Token COUNT = seq(
        rep(def("a", 1, eq(con(3)))),
        def("count", 1, eq(count(ref("a"))))
    );

    @Override
    public Collection<Object[]> data() {
        return List.of(new Object[][]{
            {"[] = count(0)", COUNT, stream(0), enc(), true},
            {"[3] = count(1)", COUNT, stream(3, 1), enc(), true},
            {"[3,3] = count(2)", COUNT, stream(3, 3, 2), enc(), true},
            {"[3,3,3] = fail", COUNT, stream(3, 3, 3, 3), enc(), false}, // fails because the rep 'eats' the 4th '3'
            {"[3,3,3,3] = count(4)", COUNT, stream(3, 3, 3, 3, 4), enc(), true},
        });
    }

}
