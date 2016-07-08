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

package io.parsingdata.metal.util;

import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.expTrue;
import static io.parsingdata.metal.Shorthand.gtNum;
import static io.parsingdata.metal.Shorthand.ltNum;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.or;

import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.logical.BinaryLogicalExpression;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
* Utility class containing useful expressions composed of metal-core expressions.
*
* @author Netherlands Forensic Institute.
*/
public final class CompoundExpression {

    private CompoundExpression() {
    }

    /**
     * False expression.
     *
     * @return expression evaluating to false
     */
    public static Expression expFalse() {
        return not(expTrue());
    }

    /**
     * Greater than equals expression, evaluating to true when current value &gt;= p.
     *
     * @param p the value to compare against
     * @return expression evaluating to true if self &gt;= p
     */
    public static BinaryLogicalExpression gtEqNum(final ValueExpression p) {
        return or(gtNum(p), eqNum(p));
    }

    /**
     * Greater than equals expression, evaluating to true when c &gt;= p.
     *
     * @param c the value to compare
     * @param p the value to compare against
     * @return expression evaluating to true if c &gt;= p
     */
    public static BinaryLogicalExpression gtEqNum(final ValueExpression c, final ValueExpression p) {
        return or(gtNum(c, p), eqNum(c, p));
    }

    /**
     * Less than equals expression, evaluating to true when current value &lt;= p.
     *
     * @param p the value to compare against
     * @return expression evaluating to true if self &lt;= p
     */
    public static BinaryLogicalExpression ltEqNum(final ValueExpression p) {
        return or(eqNum(p), ltNum(p));
    }

    /**
     * Less than equals expression, evaluating to true when c &lt;= p.
     *
     * @param c the value to compare
     * @param p the value to compare against
     * @return expression evaluating to true if c &lt;= p
     */
    public static BinaryLogicalExpression ltEqNum(final ValueExpression c, final ValueExpression p) {
        return or(eqNum(c, p), ltNum(c, p));
    }

    /**
     * Implication expression, evaluation to true when l -&gt; r (i.e., !l || r).
     *
     * @param l the antecedent of the expression
     * @param r the consequent of the expression
     * @return expression evaluating to true when l and r are both true, or l is false
     */
    public static BinaryLogicalExpression imp(final Expression l, final Expression r) {
        return or(not(l), r);
    }
}