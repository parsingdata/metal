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

package io.parsingdata.metal.expression.comparison;

import java.io.IOException;

import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

public class Eq extends ComparisonExpression {

    public Eq(final ValueExpression value, final ValueExpression predicate) {
        super(value, predicate);
    }

    @Override
    public boolean compare(final Value left, final Value right) throws IOException {
        final byte[] leftBytes = left.getValue();
        final byte[] rightBytes = right.getValue();
        if (leftBytes.length != rightBytes.length) { return false; }
        for (int i = 0; i < leftBytes.length; i++) {
            if (leftBytes[i] != rightBytes[i]) { return false; }
        }
        return true;
    }

}
