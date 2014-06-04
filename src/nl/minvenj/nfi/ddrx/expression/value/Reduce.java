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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Deque;

import nl.minvenj.nfi.ddrx.data.Environment;

public class Reduce<T extends BinaryValueExpression> implements ValueExpression {

    private final String _name;
    private final Class<T> _reducer;

    public Reduce(String name, Class<T> reducer) {
        _name = name;
        _reducer = reducer;
    }

    @Override
    public OptionalValue eval(Environment env) {
        try {
            Deque<Value> values = env.getAll(_name);
            Constructor<T> con = _reducer.getConstructor(ValueExpression.class, ValueExpression.class);
            if (values.size() > 0) {
                return reduce(env, con, OptionalValue.of(values.pop()), values);
            }
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return OptionalValue.empty();
    }

    private OptionalValue reduce(Environment env, Constructor<T> con, OptionalValue head, Deque<Value> tail) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (!head.isPresent() || tail.size() == 0) { return head; }
        return reduce(env, con, con.newInstance(con(head.get()), con(tail.pop())).eval(env), tail);
    }

}
