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

package nl.minvenj.nfi.ddrx;

import static nl.minvenj.nfi.ddrx.Shorthand.*;
import static nl.minvenj.nfi.ddrx.TokenDefinitions.any;
import static nl.minvenj.nfi.ddrx.util.EnvironmentFactory.stream;

import java.util.Arrays;
import java.util.Collection;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.expression.logical.LogicalExpression;
import nl.minvenj.nfi.ddrx.token.Token;
import nl.minvenj.nfi.ddrx.util.ParameterizedParse;

import org.junit.runners.Parameterized.Parameters;

public class LogicalExpressionSemantics extends ParameterizedParse {

    @Parameters(name="{0} ({3})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            { "2 == 2 && 2 > 1", andEqGt, stream(2, 1, 2), true },
            { "3 == 2 && 3 > 1", andEqGt, stream(2, 1, 3), false },
            { "2 == 2 && 2 > 3", andEqGt, stream(2, 3, 2), false },
            { "1 == 2 && 1 > 1", andEqGt, stream(2, 1, 1), false },
            { "1 < 2 || 1 == 1", orLtEq, stream(2, 1, 1), true },
            { "2 < 3 || 2 == 3", orLtEq, stream(3, 3, 2), true },
            { "2 < 1 || 2 == 2", orLtEq, stream(1, 2, 2), true },
            { "2 < 1 || 2 == 3", orLtEq, stream(1, 3, 2), false },
            { "!(!(3 == 1) && !(3 > 2))", notAndNotEqNotGt, stream(1, 2, 3), true },
            { "!(!(2 == 1) && !(2 > 3))", notAndNotEqNotGt, stream(1, 3, 2), false }
        });
    }

    public LogicalExpressionSemantics(String desc, Token token, Environment env, boolean result) {
        super(token, env, result);
    }

    private static Token andEqGt = logicalExp(and(eqNum(ref("a")), gtNum(ref("b"))));
    private static Token orLtEq = logicalExp(or(ltNum(ref("a")), eqNum(ref("b"))));
    private static Token notAndNotEqNotGt = logicalExp(not(and(not(eqNum(ref("a"))), not(gtNum(ref("b"))))));

    private static Token logicalExp(LogicalExpression le) {
        return seq(any("a"),
                   seq(any("b"),
                       def("c", con(1), le)));
    }

}
