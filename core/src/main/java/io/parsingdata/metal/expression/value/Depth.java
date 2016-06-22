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

package io.parsingdata.metal.expression.value;

import io.parsingdata.metal.SubStructTest;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Str;

/**
 * Count the depth of {@link Str} tokens in the graph to this point.
 * This can be used in recursion.
 * Example:<br>
 * <code>
 * seq(<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;str("dir",<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;str("dir",<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;pre(<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;def("name", 8),
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;gtNum(depth("dir"), con(1)));
 * </code>
 * @see SubStructTest#linkedList()
 */
public class Depth implements ValueExpression {

    private final String _scope;

    public Depth(final String scope) {
        _scope = scope;
    }

    @Override
    public OptionalValue eval(final Environment env, final Encoding enc) {
        ParseGraph last = env.order;
        int i = 0;
        for (ParseItem head = last.head; head != null && head.isGraph(); last = head.asGraph(), head = last.head) {
            if (head.getDefinition() instanceof Str && ((Str) head.getDefinition()).scope.equals(_scope)) {
                i++;
            }
        }
        return OptionalValue.of(ConstantFactory.createFromNumeric(i, enc));
    }
}
