package io.parsingdata.metal.expression.value.reference;

import static io.parsingdata.metal.Util.checkNotNull;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.ValueExpression;

public class Len implements ValueExpression {

    private final String _name;

    public Len(final String name) {
        _name = checkNotNull(name, "name");
    }

    @Override
    public OptionalValue eval(final Environment env, final Encoding enc) {
        final ParseValue value = env.order.get(_name);
        return OptionalValue.of(value == null ? null : ConstantFactory.createFromNumeric(value.getValue().length, new Encoding(true)));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _name + ")";
    }

}