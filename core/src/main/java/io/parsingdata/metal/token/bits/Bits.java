package io.parsingdata.metal.token.bits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.BitSet;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

public class Bits extends Token {

    private final ValueExpression _size;
    private final Bit[] _bits;

    public Bits(final String name, final ValueExpression size, final Bit[] bits) {
        super(name, null); // TODO enc
        _size = size;
        _bits = bits;
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        final OptionalValueList sizes = _size.eval(env, enc);
        if (sizes.size != 1 || !sizes.head.isPresent()) {
            return new ParseResult(false, env);
        }
        // TODO: Handle value expression results as BigInteger (#16)
        final int dataSize = sizes.head.get().asNumeric().intValue();
        if (dataSize < 0) {
            return new ParseResult(false, env);
        }
        final byte[] data = new byte[dataSize];
        if (env.input.read(env.offset, data) != data.length) {
            return new ParseResult(false, env);
        }
        final Environment newEnv = env;
        final BitSet all = BitSet.valueOf(data);

        for (int offset = 0, bit = 0; bit < _bits.length; bit++) {
            final OptionalValueList bitSizes = _bits[bit].size.eval(env, enc);
            if (bitSizes.size != 1 || !bitSizes.head.isPresent()) {
                return new ParseResult(false, env);
            }
            final int bitSize = bitSizes.head.get().asNumeric().intValue();

            final ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
            final long val = convert(all.get(all.length() - bitSize - offset, all.length() - offset));
            buffer.putLong(val);
            System.out.println(val);
            add(_bits[bit].name, newEnv, enc, buffer.array());
            offset += bitSize;
        }
        return new ParseResult(true, newEnv);
    }

    public static long convert(final BitSet bits) {
        long value = 0L;
        for (int i = 0; i < bits.length(); ++i) {
            value += bits.get(i) ? (1L << i) : 0L;
        }
        return value;
    }

    private Environment add(final String name, final Environment env, final Encoding enc, final byte[] value) {
        return env.add(new ParseValue(name, this, env.offset, value, enc));
    }
}