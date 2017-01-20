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

import java.util.Optional;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Interface for all ValueExpression implementations.
 * <p>
 * A ValueExpression is an expression that is evaluated by executing its
 * {@link #eval(Environment, Encoding)} method. It yields a list of
 * {@link Value} objects encapsulated in {@link Optional} objects (to guard
 * against <code>null</code>s).
 * <p>
 * As context, it receives the current <code>environment</code> object that
 * describes the parse state as well as the current <code>encoding</code>
 * object that describes the encoding to use when reading data from the input.
 */
public interface ValueExpression {

    ImmutableList<Optional<Value>> eval(Environment environment, Encoding encoding);

}
