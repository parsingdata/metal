package io.parsingdata.metal.token.bits;

import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.ParseResult.failure;
import static io.parsingdata.metal.data.ParseResult.success;

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.True;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

public class Bit extends Token {

    public final ValueExpression size;
    public final Expression predicate;

    public Bit(final String name, final ValueExpression size, final Expression predicate, final Encoding enc) {
        super(name, enc);
        this.size = checkNotNull(size, "size");
        this.predicate = predicate == null ? new True() : predicate;
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        final OptionalValueList sizes = size.eval(env, enc);
        if (sizes.size != 1 || !sizes.head.isPresent()) {
            return failure(env);
        }
        // TODO: Handle value expression results as BigInteger (#16)
        final int dataSize = sizes.head.get().asNumeric().intValue();
        if (dataSize < 0) {
            return failure(env);
        }
        final byte[] data = new byte[dataSize];
        if (env.input.read(env.offset, data) != data.length) {
            return failure(env);
        }
        final Environment newEnv = env.add(new ParseValue(scope, this, env.offset, data, enc)).seek(env.offset + dataSize);
        return predicate.eval(newEnv, enc) ? success(newEnv) : failure(env);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + size + "," + predicate + ")";
    }

}
