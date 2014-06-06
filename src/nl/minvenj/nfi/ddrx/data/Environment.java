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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

import nl.minvenj.nfi.ddrx.expression.value.OptionalValue;
import nl.minvenj.nfi.ddrx.expression.value.Value;

public class Environment {

    private final Deque<Value> _order;
    private final Deque<Integer> _marked;
    private final Deque<Integer> _scopes;
    private final ByteStream _input;

    public Environment(ByteStream input) {
        _order = new ArrayDeque<Value>();
        _marked = new ArrayDeque<Integer>();
        _scopes = new ArrayDeque<Integer>();
        _input = input;
    }

    public void pushScope() {
        _scopes.push(_order.size());
    }

    public void popScope() {
        _scopes.pop();
    }

    public void put(Value value) {
        _order.push(value);
    }

    public OptionalValue get(String name) {
        for (Value v : _order) {
            if (v.matches(name)) { return OptionalValue.of(v); }
        }
        return OptionalValue.empty();
    }

    public Deque<Value> getAll(String name) {
        Deque<Value> all = new ArrayDeque<Value>();
        Iterator<Value> iter = _order.descendingIterator();
        while (iter.hasNext()) {
            final Value v = iter.next();
            if (v.matches(name)) {
                all.add(v);
            }
        }
        return all;
    }

    public OptionalValue current() {
        return OptionalValue.of(_order.peek());
    }

    public List<Value> getPrefixInScope(String prefix) {
        final ArrayList<Value> result = new ArrayList<Value>();
        final int scopeSize = _order.size() - _scopes.peek();
        final Iterator<Value> iterator = _order.iterator();
        for (int i = 0; i < scopeSize && iterator.hasNext(); i++) {
            final Value v = iterator.next();
            if (v.getScope().startsWith(prefix)) { result.add(0, v); }
        }
        return result;
    }

    private void removeLast() {
        _order.pop();
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

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator<Value> i = _order.descendingIterator();
        while (i.hasNext()) {
            sb.append(i.next().getFullName());
            sb.append("\n");
        }
        return sb.toString();
    }

}
