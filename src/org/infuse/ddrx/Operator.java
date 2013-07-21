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

package org.infuse.ddrx;


public abstract class Operator implements Node {
    
    protected final int _size; // TODO: Make size an expression.
    protected final String _name;
    protected final Value _value;
    protected final Expression _exp;
    
    public Operator(String name, int size, Value value, Expression exp) {
        _name = name;
        _size = size;
        _value = value;
        _exp = exp;
    }
    
}
