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

package io.parsingdata.metal.token;

import io.parsingdata.metal.data.ParseResult;
import org.junit.Test;

import java.io.IOException;

import static io.parsingdata.metal.Shorthand.*;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class WhileTest {

    public static final Token VALUES = seq(
            def("value", con(1)),
            def("value2", con(1), gtNum(con(8)))
    );
    private static final Token WHILE = seq(VALUES, whl(VALUES, ltNum(ref("value"), con(1))));

    @Test
    public void parseAll() throws IOException {
        // two sequences of two bytes would be parsed: [0,9] and [1,10]
        // the while stops because the second 'value' is >= 1
        final ParseResult result = WHILE.parse(stream(0, 9, 1, 10, 2, 11), enc());

        assertThat(result.getEnvironment().offset, is(4L));
    }

    @Test
    public void parseFails() throws IOException {
        final ParseResult result = WHILE.parse(stream(0, 9, 0, 8), enc());

        // parsing fails because the nested token couldn't be parsed ('value2' <= 9)
        assertFalse(result.succeeded());
    }

    @Test
    public void whileWithoutExpression() throws IOException {
        // passing null as predicate make this a while(true):
        final Token trueWhile = new While(def("value", 1), null, enc());
        final ParseResult result = trueWhile.parse(stream(0), enc());

        // parsing fails because the nested def fails at the end of the stream
        assertFalse(result.succeeded());
    }

    @Test
    public void testToString() {
        final Token simpleWhile = whl(def("value", con(1)), eq(con(1)));
        final String simpleWhileString = "While(Def(\"value\",Const(Value(01)),True,), Eq(Const(Value(01))))";
        assertThat(simpleWhile.toString(), is(equalTo(simpleWhileString)));
    }

}
