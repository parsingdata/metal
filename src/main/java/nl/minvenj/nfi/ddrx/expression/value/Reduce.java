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

package nl.minvenj.nfi.ddrx.expression.value;

import static nl.minvenj.nfi.ddrx.Shorthand.con;
import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.data.ValueList;
import nl.minvenj.nfi.ddrx.encoding.Encoding;

public class Reduce implements ValueExpression {

    private final String _name;
    private final Reducer _reducer;

    public Reduce(final String name, final Reducer reducer) {
        _name = name;
        _reducer = reducer;
    }

    @Override
    public OptionalValue eval(final Environment env, final Encoding enc) {
        final ValueList values = env.order.getAll(_name).reverse();
        if (values.isEmpty()) {
            return OptionalValue.empty();
        }
        return reduce(env, enc, _reducer, OptionalValue.of(values.head), values.tail);
    }

    private OptionalValue reduce(final Environment env, final Encoding enc, final Reducer reducer, final OptionalValue head, final ValueList tail) {
        if (!head.isPresent() || tail == null || tail.isEmpty()) { return head; }
        return reduce(env, enc, reducer, reducer.reduce(con(head.get()), con(tail.head)).eval(env, enc), tail.tail);
    }

}
