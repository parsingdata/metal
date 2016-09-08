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

    @Test public void longConstant0() { checkLongConstant(0L); }
    @Property public void longConstant8(@InRange(min = "-128", max = "127") final long input) { checkLongConstant(input); }
    @Property public void longConstant16(@InRange(min = "-32768", max = "32767") final long input) { checkLongConstant(input); }
    @Property public void longConstant24(@InRange(min = "-8388608", max = "8388607") final long input) { checkLongConstant(input); }
    @Property public void longConstant32(@InRange(min = "-2147483648", max = "2147483647") final long input) { checkLongConstant(input); }
    @Property public void longConstant40(@InRange(min = "-549755813888", max = "549755813887") final long input) { checkLongConstant(input); }
    @Property public void longConstant48(@InRange(min = "-140737488355328", max = "140737488355327") final long input) { checkLongConstant(input); }
    @Property public void longConstant56(@InRange(min = "-36028797018963968", max = "36028797018963967") final long input) { checkLongConstant(input); }
    @Property public void longConstant64(@InRange(min = "-9223372036854775808", max = "9223372036854775807") final long input) { checkLongConstant(input); }

    private void checkLongConstant(final long input) {
        assertEquals(input, ConstantFactory.createFromNumeric(input, signed()).asNumeric().longValue());
        if (input >= 0) {
            assertEquals(input, ConstantFactory.createFromNumeric(input, enc()).asNumeric().longValue());
        } else {
            assertEquals(0, calculateUnsignedValue(input).compareTo(ConstantFactory.createFromNumeric(input, enc()).asNumeric()));
        }
    }

    private BigInteger calculateUnsignedValue(final long input) {
        for (int i = 8; i < 64; i+=8) {
            final long maxValue = BigInteger.valueOf(2).pow(i-1).longValue();
            if ((maxValue + input) >= 0) {
                return BigInteger.valueOf(input).add(BigInteger.valueOf(2*maxValue));
            }
        }
        return BigInteger.valueOf(input).add(TWOS_DIFF);
    }

    @Property
    public void bigIntegerConstant(@InRange(min = "-1000000000000000000", max = "1000000000000000000") final BigInteger input) {
        assertEquals(0, input.compareTo(ConstantFactory.createFromNumeric(input, signed()).asNumeric()));
        assertEquals(0, new BigInteger(1, input.toByteArray()).compareTo(ConstantFactory.createFromNumeric(input, enc()).asNumeric()));
    }

}
