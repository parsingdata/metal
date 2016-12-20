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

package io.parsingdata.metal.expression;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Interface for all Expression implementations.
 *
 * An Expression is evaluated by calling the
 * {@link #eval(Environment, Encoding)} method. Given an {@link Environment}
 * and an {@link Encoding}, the evaluation either succeeds or fails. Main use
 * of Expressions is to define predicates that need to be evaluated during
 * parsing.
 *
 * @see io.parsingdata.metal.token.Def
 * @see io.parsingdata.metal.token.Pre
 * @see io.parsingdata.metal.token.While
 */
public interface Expression {

    boolean eval(Environment environment, Encoding encoding);

}
