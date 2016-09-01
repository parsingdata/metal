package io.parsingdata.metal.expression.value;

import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.generator.InRange;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigInteger;

import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static org.junit.Assert.assertEquals;

@RunWith(JUnitQuickcheck.class)
public class ConstantFactoryTest {

    public static final BigInteger TWOS_DIFF = BigInteger.valueOf(2).add(BigInteger.valueOf(Long.MAX_VALUE)).add(BigInteger.valueOf(Long.MAX_VALUE));

    @Property(trials = 1000000)
    public void longConstant(final long input) {
        assertEquals(input, ConstantFactory.createFromNumeric(input, signed()).asNumeric().longValue());
        if (input >= 0) {
            //System.out.println("pos");
            assertEquals(input, ConstantFactory.createFromNumeric(input, enc()).asNumeric().longValue());
        } else {
            assertEquals(0, calculateUnsignedValue(input).compareTo(ConstantFactory.createFromNumeric(input, enc()).asNumeric()));
        }
    }

    @Test
    public void wo() {
        final long input = -39337942513L;
        assertEquals(0, calculateUnsignedValue(input).compareTo(ConstantFactory.createFromNumeric(input, enc()).asNumeric()));
    }

    private BigInteger calculateUnsignedValue(final long input) {
        for (int i = 8; i < 64; i+=8) {
            final long maxValue = BigInteger.valueOf(2).pow(i-1).longValue();
            if ((maxValue + input) >= 0) {
                System.out.print(i/8);
                return BigInteger.valueOf(input).add(BigInteger.valueOf(maxValue)).add(BigInteger.valueOf(maxValue));
            }
        }
        //System.out.print(8);
        return BigInteger.valueOf(input).add(TWOS_DIFF);
    }

    @Property
    public void bigIntegerConstant(@InRange(min = "-1000000000000000000", max = "1000000000000000000") final BigInteger input) {
        assertEquals(0, input.compareTo(ConstantFactory.createFromNumeric(input, signed()).asNumeric()));
        assertEquals(0, new BigInteger(1, input.toByteArray()).compareTo(ConstantFactory.createFromNumeric(input, enc()).asNumeric()));
    }

}
