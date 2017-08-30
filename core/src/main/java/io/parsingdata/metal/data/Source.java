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

package io.parsingdata.metal.data;

import java.io.IOException;
import java.math.BigInteger;

public abstract class Source {

    public Slice slice(final long offset, final BigInteger length) {
        return new Slice(this, offset, length);
    }

    protected abstract byte[] getData(long offset, int size) throws IOException;

    public boolean isAvailable(long offset, BigInteger dataSize) {
        try {
            return dataSize.intValue() == getData(offset, dataSize.intValue()).length;
        } catch (IOException e) {
            return false;
        }
    }

}
