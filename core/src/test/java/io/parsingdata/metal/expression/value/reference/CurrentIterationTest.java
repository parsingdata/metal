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

package io.parsingdata.metal.expression.value.reference;

import static io.parsingdata.metal.Shorthand.CURRENT_ITERATION;
import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.iteration;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.nth;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.when;
import static io.parsingdata.metal.Shorthand.whl;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Collection;
import java.util.List;

import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

public class CurrentIterationTest extends ParameterizedParse {

    public static final Token VALUE_EQ_ITERATION = def("value", con(1), eq(CURRENT_ITERATION));
    public static final Token VALUE_NOT_EQ_ITERATION = def("value", con(1), not(eq(CURRENT_ITERATION)));
    public static final Token VALUE_EQ_ITERATION_PARENT = def("value", con(1), eq(iteration(1)));
    public static final Token VALUE_EQ_ITERATION_GRANDPARENT = def("value", con(1), eq(iteration(2)));
    public static final Token VALUE_EQ_255 = def("value", con(1), eq(con(255)));

    @Override
    public Collection<Object[]> data() {
        return List.of(new Object[][] {
            { "[0, 1, 2, 3, 255] rep(CURRENT_ITERATION), def(255)", seq(rep(VALUE_EQ_ITERATION), VALUE_EQ_255), stream(0, 1, 2, 3, 255), enc(), true },
            { "[0, 1, 2, 3] repn=4(CURRENT_ITERATION)", repn(VALUE_EQ_ITERATION, con(4)), stream(0, 1, 2, 3), enc(), true },
            { "[255, 0, 1, 2, 3, 255] def(255), while!=3(CURRENT_ITERATION), def (255)", seq(VALUE_EQ_255, whl(VALUE_EQ_ITERATION, not(eq(con(3)))), VALUE_EQ_255), stream(255, 0, 1, 2, 3, 255), enc(), true },
            { "[0, 0, 1, 2, 255, 1, 0, 1, 2, 255] repn=2(CURRENT_ITERATION, repn=3(CURRENT_ITERATION))", repn(seq(VALUE_EQ_ITERATION, repn(VALUE_EQ_ITERATION, con(3)), VALUE_EQ_255), con(2)), stream(0, 0, 1, 2, 255, 1, 0, 1, 2, 255), enc(), true },
            { "[0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2] repn=2(CURRENT_ITERATION, repn=1(PARENT_ITERATION, repn=3(GRANDPARENT_ITERATION)))", repn(seq(VALUE_EQ_ITERATION, repn(seq(VALUE_EQ_ITERATION_PARENT, repn(VALUE_EQ_ITERATION_GRANDPARENT, con(2))), con(1))), con(3)), stream(0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 2, 2), enc(), true },
            { "[0, 1] seq(CURRENT_ITERATION, ...)", seq(VALUE_EQ_ITERATION, VALUE_EQ_ITERATION), stream(0, 1), enc(), false },
            { "[0] CURRENT_ITERATION", VALUE_EQ_ITERATION, stream(0), enc(), false },
            { "[0, 1] seq(!CURRENT_ITERATION, ...)", seq(VALUE_NOT_EQ_ITERATION, VALUE_NOT_EQ_ITERATION), stream(0, 1), enc(), true },
            { "[0] !CURRENT_ITERATION", VALUE_NOT_EQ_ITERATION, stream(0), enc(), true },
            { "[0 | 0, 1 | 0, 0, 2 | 0, 0, 0, 3] rep(CURRENT_ITERATION)", rep(def("value", add(CURRENT_ITERATION, con(1)), eqNum(CURRENT_ITERATION))), stream(0, 0, 1, 0, 0, 2, 0, 0, 0, 3), enc(), true },
            { "[1, 1, 0, 1 | 0 | 1 | 3] repn=4(def(size)), repn=4(if(sizeRef(CURRENT_ITERATION) != 0, def(CURRENT_ITERATION)))", seq(repn(def("size", 1), con(4)), repn(when(def("value", con(1), eq(CURRENT_ITERATION)), not(eq(nth(ref("size"), CURRENT_ITERATION), con(0)))), con(4))), stream(1, 1, 0, 1, 0, 1, 3), enc(), true },
        });
    }

}
