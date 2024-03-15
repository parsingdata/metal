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

import static io.parsingdata.metal.Shorthand.and;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.gtNum;
import static io.parsingdata.metal.Shorthand.ltNum;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.or;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.util.Collection;
import java.util.List;

import io.parsingdata.metal.expression.logical.LogicalExpression;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

public class LogicalExpressionSemanticsTest extends ParameterizedParse {

    private static final Token andEqGt = logicalExp(and(eqNum(ref("a")), gtNum(ref("b"))));
    private static final Token orLtEq = logicalExp(or(ltNum(ref("a")), eqNum(ref("b"))));
    private static final Token notAndNotEqNotGt = logicalExp(not(and(not(eqNum(ref("a"))), not(gtNum(ref("b"))))));

    private static Token logicalExp(final LogicalExpression le) {
        return seq(any("a"),
            any("b"),
            def("c", con(1), le));
    }

    @Override
    public Collection<Object[]> data() {
        return List.of(new Object[][] {
            { "2 == 2 && 2 > 1", andEqGt, stream(2, 1, 2), enc(), true },
            { "3 == 2 && 3 > 1", andEqGt, stream(2, 1, 3), enc(), false },
            { "2 == 2 && 2 > 3", andEqGt, stream(2, 3, 2), enc(), false },
            { "1 == 2 && 1 > 1", andEqGt, stream(2, 1, 1), enc(), false },
            { "1 < 2 || 1 == 1", orLtEq, stream(2, 1, 1), enc(), true },
            { "2 < 3 || 2 == 3", orLtEq, stream(3, 3, 2), enc(), true },
            { "2 < 1 || 2 == 2", orLtEq, stream(1, 2, 2), enc(), true },
            { "2 < 1 || 2 == 3", orLtEq, stream(1, 3, 2), enc(), false },
            { "!(!(3 == 1) && !(3 > 2))", notAndNotEqNotGt, stream(1, 2, 3), enc(), true },
            { "!(!(2 == 1) && !(2 > 3))", notAndNotEqNotGt, stream(1, 3, 2), enc(), false }
        });
    }

}
