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

import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.selection.ByName.getAllValues;
import static io.parsingdata.metal.expression.value.OptionalValue.wrap;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link ValueExpression} that represents all
 * {@link io.parsingdata.metal.expression.value.Value}s in the parse state
 * that match the provided <code>name</code>.
 * <p>
 * A matching name can be both a full name or a partial name. A matching
 * partial name means that any leading part of its scope is omitted. For
 * example, the <code>name</code> "thud.blat":
 * <ul>
 *     <li>does match: thud.blat (full name)</li>
 *     <li>does match: foo.bar.thud.blat (partial name)</li>
 *     <li>does not match: foo.barthud.blat (partial names must be cut off at scope separator boundaries)</li>
 * </ul>
 */
public class NameRef implements ValueExpression {

    public final String name;

    public NameRef(final String name) {
        this.name = checkNotNull(name, "name");
    }

    @Override
    public ImmutableList<OptionalValue> eval(final Environment environment, final Encoding encoding) {
        return wrap(getAllValues(environment.order, name));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + name + ")";
    }

}
