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

package nl.minvenj.nfi.ddrx.data;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Stack;

public class Environment {

    private final HashMap<String, Stack<BigInteger>> _vals;
    private final Stack<String> _order;
    private final Stack<Integer> _marked;

    public Environment() {
        _vals = new HashMap<String, Stack<BigInteger>>();
        _order = new Stack<String>();
        _marked = new Stack<Integer>();
    }

    public void put(String name, BigInteger value) {
        if (!_vals.containsKey(name)) {
            _vals.put(name, new Stack<BigInteger>());
        }
        _vals.get(name).push(value);
        _order.push(name);
    }
    
    public BigInteger get(String name) {
        return _vals.get(name).peek();
    }
    
    private void removeLast() {
        if (_order.size() > 0) {
            String name = _order.pop();
            _vals.get(name).pop();
            if (_vals.get(name).size() == 0) {
                _vals.remove(name);
            }
        }
    }
    
    public void mark() {
        _marked.add(_order.size());
    }
    
    public void clear() {
        _marked.pop();
    }
    
    public void reset() {
        int reset = _order.size() - _marked.pop();
        for (int i = 0; i < reset; i++) {
            removeLast();
        }
    }
    
}
