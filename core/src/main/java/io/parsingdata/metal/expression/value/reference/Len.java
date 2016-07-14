package io.parsingdata.metal.expression.value.reference;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.encoding.Sign;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.UnaryValueExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

public class Len extends UnaryValueExpression {

    public Len(final ValueExpression op) {
        super(op);
    }

    @Override
    public OptionalValue eval(final Value v, final Environment env, final Encoding enc) {
        return OptionalValue.of(num(v.getValue().length));
    }

    private static Value num(final long length) {
        return ConstantFactory.createFromNumeric(length, new Encoding(Sign.SIGNED));
    }
}
