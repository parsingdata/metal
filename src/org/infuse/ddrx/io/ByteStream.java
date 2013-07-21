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

package org.infuse.ddrx.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Stack;

public class ByteStream {
    
    private byte[] _data;
    private int _offset;
    private Stack<Integer> _marked;
    
    public ByteStream(String path) throws IOException {
        _data = Files.readAllBytes(Paths.get(path));
        _offset = 0;
        _marked = new Stack<Integer>();
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
    
    private int pop() {
        if (_marked.isEmpty()) {
            throw new RuntimeException("Stream was not marked.");
        }
        return _marked.pop();
    }
    
    public int read() {
        if (atEnd()) {
            throw new RuntimeException("End of stream reached.");
        }
        int ret = _data[_offset];
        _offset++;
        return ret;
    }
    
    public boolean atEnd() {
        return _offset >= _data.length;
    }
    
}
