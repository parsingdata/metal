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

package org.infuse.ddrx.expression;

import java.math.BigInteger;

public class Predicate {
    
    private final Expression _exp;
    private final Operator _op;
    
    public Predicate(Expression exp, Operator op) {
        _exp = exp;
        _op = op;
    }
    
    public boolean check(BigInteger input) {
        int res = input.compareTo(_exp.eval());
        switch (_op) {
        case Equals:
            return res == 0;
        case NotEquals:
            return res != 0;
        case GreaterThan:
            return res > 0;
        case LessThan:
            return res < 0;
        case GreaterThanOrEquals:
            return res >= 0;
        case LessThanOrEquals:
            return res <= 0;
        }
        throw new RuntimeException("Unknown operator: " + _op);
    }

}
