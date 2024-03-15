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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.gtNum;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.Collection;
import java.util.List;

import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

public class IterateTest extends ParameterizedParse {

    private static final Token repNToken =
        seq(any("n"),
            repn(def("x", con(1), gtNum(con(1))), last(ref("n"))),
            def("f", con(1), eq(con(42))));

    private static final Token repBrokenNToken =
        seq(repn(any("x"), div(con(1), con(0))),
            def("f", con(1), eq(con(42))));

    @Override
    public Collection<Object[]> data() {
        return List.of(new Object[][] {
            { "[3, 2, 3, 4, 42] n, repN(x>1, n), f", repNToken, stream(3, 2, 3, 4, 42), enc(), true },
            { "[3, 2, 3, 4, 41] n, repN(x>1, n), f", repNToken, stream(3, 2, 3, 4, 41), enc(), false },
            { "[3, 2, 3, 1, 42] n, repN(x>1, n), f", repNToken, stream(3, 2, 3, 1, 42), enc(), false },
            { "[3, 2, 3, 42] n, repN(x>1, n), f", repNToken, stream(3, 2, 3, 42), enc(), false },
            { "[0, 42] n, repN(x>1, n), f", repNToken, stream(0, 42), enc(), true },
            { "[-1, 42] n, repN(x>1, n), f", repNToken, stream(-1, 42), signed(), true },
            { "[42], repN(x, 1/0), f", repBrokenNToken, stream(42), enc(), false },
            { "[0, 42], repN(x, 1/0), f", repBrokenNToken, stream(0, 42), enc(), false }
        });
    }

}
