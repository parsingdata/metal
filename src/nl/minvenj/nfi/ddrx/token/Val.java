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

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.expression.Expression;
import nl.minvenj.nfi.ddrx.expression.value.NumericValue;
import nl.minvenj.nfi.ddrx.expression.value.ValueExpression;

public class Val implements Token, ValueExpression<Val> {

    private String _name;
    private ValueExpression<NumericValue> _size;
    private Expression _pred;
    protected byte[] _data;
    
    public Val(String name, ValueExpression<NumericValue> size, Expression pred) {
        _name = name;
        _size = size;
        _pred = pred;
    }
    
    public Val(byte[] data) {
        _data = data;
    }

    @Override
    public boolean parse(Environment env) {
        int size = _size.eval(env).toBigInteger().intValue();
        _data = new byte[size];
        env.mark();
        try {
            if (env.read(_data) != size) {
                env.reset();
                return false;
            }
            env.put(this);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException(t);
        }
        if (_pred.eval(env)) {
            env.clear();
            return true;
        } else {
            env.reset();
            return false;
        }
    }
    
    public String getName() {
        return _name;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(\"" + _name + "\"," + _size + "," + _pred + ",)";
    }

    @Override
    public Val eval(Environment env) {
        return this;
    }
    
}
