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

import nl.minvenj.nfi.ddrx.data.ValueStore;
import nl.minvenj.nfi.ddrx.expression.Expression;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;
import nl.minvenj.nfi.ddrx.io.ByteStream;

public class Value implements Token {

    private final String _name;
    private final ValueExpression _size;
    private final Expression _pred;

    public Value(String name, ValueExpression size, Expression pred) {
        _name = name;
        _size = size;
        _pred = pred;
    }

    @Override
    public boolean eval(ByteStream input) {
        // Evaluate size.
        BigInteger size = _size.eval();
        // Read size from stream.
        byte[] data = new byte[size.intValue()];
        input.mark();
        try {
            if (input.read(data) != size.intValue()) {
                input.reset();
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
        ValueStore.getInstance().put(_name, value);
        if (_pred.eval()) {
            ValueStore.getInstance().finalize(_name);
            input.clear();
            return true;
        } else {
            ValueStore.getInstance().revoke(_name);
            input.reset();
            return false;
        }
        // TODO: If not, store predicate.
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(\"" + _name + "\"," + _size + "," + _pred + ",)";
    }
    
}
