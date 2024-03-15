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

import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.CURRENT_OFFSET;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.token.Token;

public class CurrentOffsetTest {

    @Test
    public void currentOffset() {
        final Optional<ParseState> result =
            seq(
                prePostDef("zero", 0),
                sub(prePostDef("three", 3), con(3)),
                prePostDef("one", 1),
                tie(prePostDef("zeroInOne", 0), last(ref("one"))),
                prePostDef("two", 2)
            ).parse(env(stream(0, 1, 2, 3, 4)));
        assertTrue(result.isPresent());
    }

    private Token prePostDef(final String name, final int offset) {
        return
            post(pre(def(name, 1), eq(CURRENT_OFFSET, con(offset))), eq(CURRENT_OFFSET, con(offset+1)));
    }

}
