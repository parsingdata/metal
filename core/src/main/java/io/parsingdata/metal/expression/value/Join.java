package io.parsingdata.metal.expression.value;

import static io.parsingdata.metal.Util.checkContainsNoNulls;

import java.util.Arrays;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

/**
 * A {@link ValueExpression} that joins multiple {@link ValueExpression}s by concatenating
 * the individual results to a single list.
 * <p>
 * A Join expression can have zero or more expressions. If none is provided, this will return an empty list.
 * Else, each expression is evaluated and concatenated to a single list.
 */
public class Join implements ValueExpression {

    private final ValueExpression[] expressions;

    public Join(final ValueExpression... expressions) {
        this.expressions = checkContainsNoNulls(expressions, "expression");
    }

    @Override
    public ImmutableList<Value> eval(final ParseState parseState, final Encoding encoding) {
        return Arrays.stream(expressions)
            .map(e -> e.eval(parseState, encoding))
            .peek(System.out::println)
            .reduce(new ImmutableList<>(), ImmutableList::add, ImmutableList::add);
    }
}
