package io.parsingdata.metal.expression.value;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Expression for the 'elvis operator': <pre>?:</pre>.
 * <p>
 * Example:
 *
 * <pre>
 *   elvis(ref("foo"), ref("bar"))
 * </pre>
 *
 * If <code>ref("foo")</code> can be successfully evaluated, this elvis-expression
 * evaluates to that value, else it evaluates to the value of <code>ref("bar")</code>.
 */
public class Elvis implements ValueExpression {
    private final ValueExpression _lop;
    private final ValueExpression _rop;

    public Elvis(final ValueExpression lop, final ValueExpression rop) {
        _lop = lop;
        _rop = rop;
    }

    @Override
    public OptionalValue eval(final Environment env, final Encoding enc) {
        final OptionalValue eval = _lop.eval(env, enc);
        if (eval.isPresent()) {
            return eval;
        }
        return _rop.eval(env, enc);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _lop + "," + _rop + ")";
    }
}
