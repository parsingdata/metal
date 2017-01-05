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

/**
 * Interface for Reducer implementations used by {@link FoldLeft} and
 * {@link FoldRight}.
 * <p>
 * A Reducer has a single method
 * {@link #reduce(ValueExpression, ValueExpression)} that accepts two operands
 * <code>left</code> and <code>right</code> (both {@link ValueExpression}). It
 * should return a ValueExpression that composes both operands.
 */
public interface Reducer {

    ValueExpression reduce(ValueExpression left, ValueExpression right);

}
