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

package nl.minvenj.nfi.ddrx.token;

import java.io.IOException;
import java.math.BigInteger;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.expression.Expression;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;

public class Val implements Token {

    private final String _name;
    private final ValueExpression _size;
    private final Expression _pred;

    public Val(String name, ValueExpression size, Expression pred) {
        _name = name;
        _size = size;
        _pred = pred;
    }

    @Override
    public boolean eval(Environment env) {
        // Evaluate size.
        BigInteger size = _size.eval(env);
        // Read size from stream.
        byte[] data = new byte[size.intValue()];
        env.mark();
        try {
            if (env.read(data) != size.intValue()) {
                env.reset();
                return false;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        BigInteger value = new BigInteger(data);
        // TODO: Validate type.
        // TODO: Determine if stored predicates can be evaluated.
        // TODO: If so, evaluate stored predicates and return false if one fails.
        // TODO: Determine if predicate can be evaluated.
        // If so, evaluate and return result.
        env.put(_name, value);
        if (_pred.eval(env)) {
            env.clear();
            return true;
        } else {
            env.reset();
            return false;
        }
        // TODO: If not, store predicate.
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(\"" + _name + "\"," + _size + "," + _pred + ",)";
    }
    
}
