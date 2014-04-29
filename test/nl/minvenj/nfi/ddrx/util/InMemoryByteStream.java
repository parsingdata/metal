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

package nl.minvenj.nfi.ddrx.util;

import java.io.IOException;
import java.util.Stack;

import nl.minvenj.nfi.ddrx.data.ByteStream;

public class InMemoryByteStream implements ByteStream {

    private final byte[] _data;
    private final Stack<Integer> _marked;
    private int _offset;

    public InMemoryByteStream(byte[] data) {
        _data = data;
        _marked = new Stack<Integer>();
        _offset = 0;
    }

    public void mark() {
        _marked.add(_offset);
    }

    public void reset() {
        _offset = pop();
    }

    public void clear() {
        pop();
    }

    public int read(byte[] data) throws IOException {
        if (_offset >= _data.length) { return 0; }
        int toCopy = _offset + data.length > _data.length ? _data.length - _offset : data.length;
        System.arraycopy(_data, _offset, data, 0, toCopy);
        _offset += toCopy;
        return toCopy;
    }

    private int pop() {
        if (_marked.isEmpty()) { throw new RuntimeException("Stream was not marked."); }
        return _marked.pop();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _data.length + ")";
    }

}
