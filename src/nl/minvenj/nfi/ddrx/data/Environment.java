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

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

import nl.minvenj.nfi.ddrx.encoding.Encoding;
import nl.minvenj.nfi.ddrx.expression.value.Value;

public class Environment {

    private final HashMap<String, Stack<Value>> _vals;
    private final Stack<String> _order;
    private final Stack<Integer> _marked;
    private final ByteStream _input;
    private final Encoding _encoding;

    public Environment(Encoding encoding, ByteStream input) {
        _vals = new HashMap<String, Stack<Value>>();
        _order = new Stack<String>();
        _marked = new Stack<Integer>();
        _input = input;
        _encoding = encoding;
    }

    public Encoding getEncoding() {
        return _encoding;
    }

    private Stack<Value> getStack(String name) {
        if (!_vals.containsKey(name)) {
            _vals.put(name, new Stack<Value>());
        }
        return _vals.get(name);
    }

    public void put(Value value) {
        getStack(value.getName()).push(value);
        _order.push(value.getName());
    }

    public Value get(String name) {
        return _vals.containsKey(name) ? _vals.get(name).peek() : null;
    }

    public Value current() {
        return _order.isEmpty() ? null : get(_order.peek());
    }

    private void removeLast() {
        if (_order.size() > 0) {
            final String name = _order.pop();
            _vals.get(name).pop();
            if (_vals.get(name).size() == 0) {
                _vals.remove(name);
            }
        }
    }

    public void mark() {
        _input.mark();
        _marked.add(_order.size());
    }

    public void clear() {
        _input.clear();
        _marked.pop();
    }

    public void reset() {
        _input.reset();
        final int reset = _order.size() - _marked.pop();
        for (int i = 0; i < reset; i++) {
            removeLast();
        }
    }

    public int read(byte[] data) throws IOException {
        return _input.read(data);
    }

}
