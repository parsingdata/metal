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
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static io.parsingdata.metal.Shorthand.*;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

public class ConditionalTokenTest extends ParameterizedParse {

    @Parameters(name = "{0} ({4})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
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

    public ConditionalTokenTest(final String name, final Token token, final Environment env, final Encoding enc, final boolean result) {
        super(token, env, enc, result);
    }

    private static final Token preToken = seq(any("a"),
                                              pre(any("b"), eqNum(ref("a"), con(1))),
                                              def("c", con(1), eqNum(con(3))));
    
    private static final Token whileToken = seq(any("size"),
                                                whl(any("value"), ltNum(currentOffset, add(ref("size"), add(offset(last(ref("size"))), con(1))))),
                                                def("footer", con(1), eq(con(0xff))));

}
