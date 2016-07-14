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

package io.parsingdata.metal.expression.value;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.encoding.Encoding;

public class Const implements ValueExpression {

    private final Value _val;

    public Const(final Value val) {
        _val = val;
    }

    @Override
    public OptionalValueList eval(final Environment env, final Encoding enc) {
        return OptionalValueList.create(OptionalValue.of(_val));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _val.toString() + ")";
    }

}
