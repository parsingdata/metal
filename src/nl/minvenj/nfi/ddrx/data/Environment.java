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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Stack;

import nl.minvenj.nfi.ddrx.encoding.Encoding;
import nl.minvenj.nfi.ddrx.expression.value.Value;

public class Environment {

    private final HashMap<String, ValueStack<?>> _vals;
    private final Stack<String> _order;
    private final Stack<Integer> _marked;
    private final ByteStream _input;
    private final Encoding _encoding;

    public Environment(ByteStream input) {
        this(input, new Encoding());
    }

    public Environment(ByteStream input, Encoding encoding) {
        _vals = new HashMap<String, ValueStack<?>>();
        _order = new Stack<String>();
        _marked = new Stack<Integer>();
        _input = input;
        _encoding = encoding;
    }

    public Encoding getEncoding() {
        return _encoding;
    }

    @SuppressWarnings("unchecked")
    private <T extends Value> ValueStack<T> getStack(Class<T> valueClass, String name) {
        if (!_vals.containsKey(name)) {
            _vals.put(name, new ValueStack<T>(valueClass));
        }
        return (ValueStack<T>) _vals.get(name);
    }

    @SuppressWarnings("unchecked")
    private <T extends Value> Class<T> classOf(T value) {
        return (Class<T>) value.getClass();
    }

    public <T extends Value> void put(T value) {
        getStack(classOf(value), value.getName()).push(value);
        _order.push(value.getName());
    }

    @SuppressWarnings("unchecked")
    public <T extends Value> T get(String name) {
        return _vals.containsKey(name) ? (T) _vals.get(name).peek() : null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Value> T current() {
        return _order.isEmpty() ? null : (T) get(_order.peek());
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

    public static Environment stream(int... bytes) {
        return new Environment(new ByteStream(toByteArray(bytes)));
    }

    public static Environment stream(Path path) throws IOException {
        return new Environment(new ByteStream(Files.readAllBytes(path)));
    }

    public static Environment stream(String value) {
        return new Environment(new ByteStream(value.getBytes()));
    }

    public static byte[] toByteArray(int... bytes) {
        final byte[] out = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            out[i] = (byte) bytes[i];
        }
        return out;
    }

}
