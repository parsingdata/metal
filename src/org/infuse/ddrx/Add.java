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

import java.math.BigInteger;

public class Add extends Expression {
    
    private final Expression _l;
    private final Expression _r;
    
    public Add(Expression l, Expression r) {
        _l = l;
        _r = r;
    }
    
    @Override
    public BigInteger eval() {
        return _l.eval().add(_r.eval());
    }
    
}
