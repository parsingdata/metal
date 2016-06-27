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
package io.parsingdata.metal.util.serialize.constraint;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.GraphUtil;

/**
 * Used to constrain a transformation.
 *
 * Examples are:
 * - transform within IPv4 that contains UDP
 * - transform within IPv6 that contains HTTP and was sent over TCP port 443
 *
 * This is independent of the actual transformation.
 *
 * @author Netherlands Forensic Institute.
 */
public class TransformConstraint {

    /** Constraint which is always satisfied. */
    public static final TransformConstraint TRUE = new TransformConstraint();

    private final Token[] _tokens;
    private final Expression _expression;

    /**
     * Same as {@link #TransformConstraint(Expression, Token...)} with null as expression.
     *
     * @param tokens the context which should be present
     */
    public TransformConstraint(final Token... tokens) {
        // TODO: never accept "Def" in constraint tokens
        _tokens = tokens;
        _expression = null;
    }

    /**
     * Initialize a new transformer constraint.
     *
     * A transformer constraint demands that a certain context is present.
     * It optionally can also demand a certain expression to be true in this context.
     *
     * @param expression the expression to evaluate within the given context
     * @param tokens the context which should be present
     */
    public TransformConstraint(final Expression expression, final Token... tokens) {
        _tokens = tokens;
        _expression = expression;
    }

    /**
     * Check if the given environment satisfies this constraint.
     *
     * @param environment an environment to check the constraint on
     * @return true if the constraint is satisfied by the given environment
     */
    public boolean isSatisfiedBy(final Environment environment) {
        return isSatisfiedBy(_tokens, environment) && isSatisfiedBy(_expression, environment);
    }

    private boolean isSatisfiedBy(final Token[] definitions, final Environment environment) {
        return GraphUtil.containsDefinitions(environment.order, definitions);
    }

    private boolean isSatisfiedBy(final Expression expression, final Environment environment) {
        return expression == null || expression.eval(environment, new Encoding()); // TODO PEF-24 which encoding
    }
}
