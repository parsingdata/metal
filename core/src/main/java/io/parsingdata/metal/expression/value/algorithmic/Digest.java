package io.parsingdata.metal.expression.value.algorithmic;

import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;
import static java.security.MessageDigest.getInstance;

import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.Slice.createFromBytes;
import static io.parsingdata.metal.data.Slice.createFromSource;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.Slice;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.CoreValue;
import io.parsingdata.metal.expression.value.UnaryValueExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * Calculate the hash of a {@link ValueExpression}.
 */
public class Digest extends UnaryValueExpression {

    public final String algorithm;

    /**
     * Calculate the hash of a {@link ValueExpression}.
     *
     * @param algorithm The hash algorithm for {@link MessageDigest#getInstance(String)}.
     * @param operand The ValueExpression to digest
     */
    public Digest(final String algorithm, final ValueExpression operand) {
        super(operand);
        this.algorithm = checkNotNull(algorithm, "algorithm");
    }

    @Override
    public Optional<Value> eval(final Value value, final ParseState parseState, final Encoding encoding) {
        try {
            final Slice slice = value.slice();
            final BigInteger bufferSize = valueOf(512);
            final MessageDigest digester = getInstance(algorithm);

            for (BigInteger i = ZERO; i.compareTo(slice.length) < 0; i = i.add(bufferSize)) {
                final BigInteger length = bufferSize.min(slice.length.subtract(i));
                final Optional<Slice> subslice = createFromSource(slice.source, slice.offset.add(i), length);
                if (subslice.isEmpty()) {
                    return Optional.empty();
                }
                digester.update(subslice.get().getData());
            }

            return Optional.of(new CoreValue(createFromBytes(encoding.byteOrder.apply(digester.digest())), encoding));
        }
        catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
