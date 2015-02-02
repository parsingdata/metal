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

package nl.minvenj.nfi.metal.expression.value.reference;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.encoding.Encoding;
import nl.minvenj.nfi.metal.expression.value.ConstantFactory;
import nl.minvenj.nfi.metal.expression.value.OptionalValue;
import nl.minvenj.nfi.metal.expression.value.ParsedValue;
import nl.minvenj.nfi.metal.expression.value.ValueExpression;

public class Offset implements ValueExpression {
    
    private final String _name;
    
    public Offset(final String name) {
        _name = name;
    }

    @Override
    public OptionalValue eval(final Environment env, final Encoding enc) {
        final ParsedValue ref = env.order.get(_name);
        return ref != null ? OptionalValue.of(ConstantFactory.createFromNumeric(ref.getOffset(), ref.getEncoding())) : OptionalValue.empty();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _name + ")";
    }

}
