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

package nl.minvenj.nfi.ddrx.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

public class ByteStream extends InputStream {
    
    private byte[] _data;
    private int _offset;
    private Stack<Integer> _marked;
    
    public ByteStream(String path) throws IOException {
        this(Files.readAllBytes(Paths.get(path)));
    }
    
    public ByteStream(byte[] data) {
        _data = data;
        _offset = 0;
        _marked = new Stack<Integer>();
    }
    
    @Override
    public boolean markSupported() {
        return true;
    }
    
    @Override
    public synchronized void mark(int readlimit) {
        _marked.add(_offset);
    }
    
    public void mark() {
        mark(0);
    }
    
    @Override
    public void reset() {
        _offset = pop();
    }
    
    @Override
    public int available() throws IOException {
        return atEnd() ? 0 : _data.length - _offset;
    }
    
    public void clear() {
        pop();
    }
    
    private int pop() {
        if (_marked.isEmpty()) {
            throw new RuntimeException("Stream was not marked.");
        }
        return _marked.pop();
    }
    
    public int read() {
        return atEnd() ? -1 : _data[_offset++] & 0xFF;
    }
    
    public boolean atEnd() {
        return _offset >= _data.length;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _data.length + ")";
    }
    
}
