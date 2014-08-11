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

import java.util.Deque;

import nl.minvenj.nfi.ddrx.data.Environment;

public class Reduce implements ValueExpression {

    private final String _name;
    private final Reducer _reducer;

    public Reduce(String name, Reducer reducer) {
        _name = name;
        _reducer = reducer;
    }

    @Override
    public OptionalValue eval(Environment env) {
        Deque<Value> values = env.getAll(_name);
        if (values.size() == 0) {
            return OptionalValue.empty();
        }
        return reduce(env, _reducer, OptionalValue.of(values.pop()), values);
    }

    private OptionalValue reduce(Environment env, Reducer reducer, OptionalValue head, Deque<Value> tail) {
        if (!head.isPresent() || tail.size() == 0) { return head; }
        return reduce(env, reducer, reducer.reduce(con(head.get()), con(tail.pop())).eval(env), tail);
    }

}