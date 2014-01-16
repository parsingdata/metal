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

package org.infuse.ddrx.exp.imp;

import java.math.BigInteger;

import org.infuse.ddrx.exp.ValueExpression;

public class Con implements ValueExpression {
    
    private final BigInteger _val;
    
    public Con(BigInteger val) {
        _val = val;
    }

    @Override
    public BigInteger eval() {
        return _val;
    }

}
