/**
 * Copyright 2016 National Cyber Security Centre, Netherlands Forensic Institute
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
package io.parsingdata.metal;

import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.expTrue;
import static io.parsingdata.metal.Shorthand.gtNum;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.or;

import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.logical.LogicalExpression;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * Utility class containing custom expressions not contained in the Metal library (yet).
 *
 * @author Netherlands Forensic Institute.
 */
public final class CustomExpression {

    private CustomExpression() {
    }

    public static LogicalExpression gtEqNum(final ValueExpression p) {
        return or(gtNum(p), eqNum(p));
    }

    public static LogicalExpression gtEqNum(final ValueExpression c, final ValueExpression p) {
        return or(gtNum(c, p), eqNum(c, p));
    }

    public static Expression expFalse() {
        return not(expTrue());
    }
}
