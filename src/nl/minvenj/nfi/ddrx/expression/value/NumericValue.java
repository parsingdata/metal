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

package nl.minvenj.nfi.ddrx.expression.value;

import java.math.BigInteger;

public class NumericValue extends Value {
    
    public NumericValue(String name, byte[] data) {
        super(name, data);
    }
    
    public NumericValue(BigInteger value) {
        super(value.toByteArray());
    }
    
    public NumericValue operation(NumericValueOperation op) {
        return op.execute(toBigInteger());
    }
    
    public int compareTo(Value other) {
        if (other instanceof NumericValue) {
            return toBigInteger().compareTo(((NumericValue)other).toBigInteger());
        }
        return super.compareTo(other);
    }
    
    public BigInteger toBigInteger() {
        return new BigInteger(_data);
    }
    
}
