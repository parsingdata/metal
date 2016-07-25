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

package io.parsingdata.metal;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;
import org.junit.Test;

import java.io.IOException;

import static io.parsingdata.metal.Shorthand.*;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ShorthandsTest {

    private static final Token multiSequence =
        seq(def("a", con(1), eq(con(1))),
            def("b", con(1), eq(con(2))),
            def("c", con(1), eq(con(3))));

    @Test
    public void sequenceMultiMatch() throws IOException {
        assertTrue(multiSequence.parse(stream(1, 2, 3), enc()).succeeded);
    }

    @Test
    public void sequenceMultiNoMatch() throws IOException {
        assertFalse(multiSequence.parse(stream(1, 2, 2), enc()).succeeded);
    }

    private static final Token multiChoice =
        cho(def("a", con(1), gtNum(con(2))),
            def("b", con(1), gtNum(con(1))),
            def("c", con(1), gtNum(con(0))));

    @Test
    public void choiceMultiMatchA() throws IOException {
        runChoice(3, "a");
    }

    @Test
    public void choiceMultiMatchB() throws IOException {
        runChoice(2, "b");
    }

    @Test
    public void choiceMultiMatchC() throws IOException {
        runChoice(1, "c");
    }

    private void runChoice(final int data, final String matched) throws IOException {
        final ParseResult res = multiChoice.parse(stream(data), enc());
        assertTrue(res.succeeded);
        assertTrue(res.environment.order.current().matches(matched));
    }

    @Test
    public void choiceMultiNoMatch() throws IOException {
        assertFalse(multiChoice.parse(stream(0), enc()).succeeded);
    }

    private static final Token nonLocalCompare =
        seq(any("a"),
            def("b", con(3)),
            def("c", con(1), gtNum(last(ref("a")), con(0))),
            def("d", con(1), eqStr(last(ref("b")), con("abc")))
        );

    @Test
    public void nonLocalCompare() throws IOException {
        assertTrue(nonLocalCompare.parse(stream(1, 'a', 'b', 'c', 0, 0), enc()).succeeded);
    }

    @Test
    public void allTokensNamed() throws IOException {
        final ParseResult result =
            str("str",
                rep("rep",
                    repn("repn",
                        seq("seq",
                            pre("pre",
                                opt("opt",
                                    any("a")),
                                expTrue()),
                            cho("cho",
                                def("def0", con(1), eq(con(0))),
                                def("def1", con(1), eq(con(1)))),
                            sub("sub",
                                def("def2", con(1), eq(con(2))),
                                con(2))
                        ), con(1)
                    )
                )
            ).parse(stream(2, 1, 2), enc());
        assertTrue(result.succeeded);
        checkNameAndValue("str.rep.repn.seq.pre.opt.a", 2, result.environment);
        checkNameAndValue("str.rep.repn.seq.cho.def1", 1, result.environment);
        checkNameAndValue("str.rep.repn.seq.sub.def2", 2, result.environment);
    }

    private void checkNameAndValue(final String name, final int value, final Environment env) {
        OptionalValueList refList = ref(name).eval(env, enc());
        assertFalse(refList.isEmpty());
        assertEquals(1, refList.size);
        assertEquals(value, refList.head.get().asNumeric().intValue());
    }

}
