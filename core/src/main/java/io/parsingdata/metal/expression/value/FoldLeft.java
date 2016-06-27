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
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.data.ParseValueList;
import io.parsingdata.metal.encoding.Encoding;

public class FoldLeft implements ValueExpression {

    private final ValueExpression _values;
    private final Reducer _reducer;
    private final ValueExpression _init;

    public FoldLeft(final ValueExpression values, final Reducer reducer, final ValueExpression init) {
        _values = checkNotNull(values, "values");
        _reducer = checkNotNull(reducer, "reducer");
        _init = init;
    }

    @Override
    public OptionalValueList eval(final Environment env, final Encoding enc) {
        final OptionalValueList init = _init != null ? _init.eval(env, enc) : OptionalValueList.EMPTY;
        if (init.size > 1) { throw new RuntimeException("Init may not contain more than a single value."); }
        final OptionalValueList values = _values.eval(env, enc).reverse();
        if (values.isEmpty() || values.containsEmpty()) { return init; }
        if (!init.isEmpty()) { return OptionalValueList.create(fold(env, enc, _reducer, init.head, values)); }
        return OptionalValueList.create(fold(env, enc, _reducer, values.head, values.tail));
    }

    private OptionalValue fold(final Environment env, final Encoding enc, final Reducer reducer, final OptionalValue head, final OptionalValueList tail) {
        if (!head.isPresent() || tail.isEmpty()) { return head; }
        return fold(env, enc, reducer, reducer.reduce(con(head.get()), con(tail.head.get())).eval(env, enc).head, tail.tail);
    }

}
