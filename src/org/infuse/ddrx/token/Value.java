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

package org.infuse.ddrx.token;

import java.math.BigInteger;

import org.infuse.ddrx.expression.Expression;
import org.infuse.ddrx.expression.value.ValueExpression;

public class Value implements Token {
    
    private final String _name;
    private final ValueExpression _size;
    private final Expression _pred;
    
    public Value(String name, ValueExpression size, Expression pred) {
        _name = name;
        _size = size;
        _pred = pred;
    }

    @Override
    public boolean eval() {
        // Evaluate size.
      BigInteger size = _size.eval();
        // Read size from stream.
        // Validate type.
        // Determine if stored predicates can be evaluated.
        // If so, evaluate stored predicates and return false if one fails.
        // Determine if predicate can be evaluated.
        // If so, evaluate and return result.
        // If not, store predicate.
        return false;
    }
    
}
