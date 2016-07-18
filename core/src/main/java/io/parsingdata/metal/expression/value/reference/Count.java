package io.parsingdata.metal.expression.value.reference;

import static io.parsingdata.metal.Util.checkNotNull;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.encoding.Sign;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

public class Count implements ValueExpression {

    public final ValueExpression op;

    public Count(final ValueExpression op) {
        this.op = checkNotNull(op, "op");
    }

    @Override
    public OptionalValueList eval(final Environment env, final Encoding enc) {
        final OptionalValueList ovl = this.op.eval(env, enc);
        return OptionalValueList.create(OptionalValue.of(num(ovl.size)));
    }

    private static Value num(final long length) {
        return ConstantFactory.createFromNumeric(length, new Encoding(Sign.SIGNED));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + op + ")";
    }

}
