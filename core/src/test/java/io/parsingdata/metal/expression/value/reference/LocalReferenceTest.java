/*
 * Copyright 2013-2018 Netherlands Forensic Institute
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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.count;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.token.Token;

public class LocalReferenceTest {

    private static final Token checkedRep =
        seq("checkedRep",
            def("magic", con(1), eq(con(42))),
            rep("items",
                def("ten", con(1), eq(con(10)))
            ),
            def("tenCount", con(1), eqNum(count(ref("ten"))))
        );

    private static final Token repTwice =
        seq("container",
            checkedRep,
            checkedRep
    );

    // Currently fails, should be resolved by implementing #250.
    @Test
    public void testLocalCount() {
        Optional<ParseState> result = repTwice.parse(env(stream(42, 10, 10, 2, 42, 10, 10, 10, 3)));
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(9, result.get().offset.intValueExact());
    }

}