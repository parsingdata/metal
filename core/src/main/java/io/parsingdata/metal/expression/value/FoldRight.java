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

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Util.checkNotNull;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseValueList;
import io.parsingdata.metal.encoding.Encoding;

public class FoldRight implements ValueExpression {

    private final String _name;
    private final Reducer _reducer;
    private final ValueExpression _init;

    public FoldRight(final String name, final Reducer reducer, final ValueExpression init) {
        _name = checkNotNull(name, "name");
        _reducer = checkNotNull(reducer, "reducer");
        _init = init;
    }

    @Override
    public OptionalValue eval(final Environment env, final Encoding enc) {
        final OptionalValue init = _init != null ? _init.eval(env, enc) : OptionalValue.empty();
        final ParseValueList values = env.order.getAll(_name);
        if (values.isEmpty()) { return init; }
        if (init.isPresent()) { return fold(env, enc, _reducer, init, values); }
        return fold(env, enc, _reducer, OptionalValue.of(values.head), values.tail);
    }

    private OptionalValue fold(final Environment env, final Encoding enc, final Reducer reducer, final OptionalValue head, final ParseValueList tail) {
        if (!head.isPresent() || tail.isEmpty()) { return head; }
        return fold(env, enc, reducer, reducer.reduce(con(tail.head), con(head.get())).eval(env, enc), tail.tail);
    }

}
