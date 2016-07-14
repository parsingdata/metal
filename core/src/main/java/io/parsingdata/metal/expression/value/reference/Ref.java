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

package io.parsingdata.metal.expression.value.reference;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.data.selection.ByName;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.ValueExpression;

import static io.parsingdata.metal.Util.checkNotNull;

public class Ref implements ValueExpression {

    private final String _name;

    public Ref(final String name) {
        _name = checkNotNull(name, "name");
    }

    @Override
    public OptionalValueList eval(final Environment env, final Encoding enc) {
        return OptionalValueList.create(ByName.getAll(env.order, _name));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _name + ")";
    }

}
