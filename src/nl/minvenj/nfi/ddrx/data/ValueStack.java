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

import java.util.Stack;

import nl.minvenj.nfi.ddrx.token.Val;

public class ValueStack<T extends Val> {
    private final Class<T> _valueClass;
    private final Stack<T> _stack;
    
    public ValueStack(Class<T> valueClass) {
        _valueClass = valueClass;
        _stack = new Stack<T>();
    }
    
    public void push(T value) {
        _stack.push(_valueClass.cast(value));
    }
    
    public T peek() {
        return _stack.peek();
    }
    
    public T pop() {
        return _stack.pop();
    }
    
    public int size() {
        return _stack.size();
    }
}
