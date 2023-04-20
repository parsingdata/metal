package io.parsingdata.metal.expression.value;

import static io.parsingdata.metal.AutoEqualityTest.DUMMY_STREAM;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.exp;
import static io.parsingdata.metal.Shorthand.join;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.expression.value.BytesTest.EMPTY_PARSE_STATE;
import static io.parsingdata.metal.expression.value.ExpandTest.createParseValue;
import static io.parsingdata.metal.expression.value.NotAValue.NOT_A_VALUE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * value expression
 * not a value expression
 * join value and lists
 * join lists
 * join lists with not a value
 * join 3 lists
 */
public class JoinTest {

    public static final ParseState PARSE_STATE = createFromByteStream(DUMMY_STREAM)
        .add(createParseValue("b", 5))
        .add(createParseValue("b", 6))
        .add(createParseValue("c", 7))
        .add(createParseValue("c", 8))
        .add(createParseValue("c", 9))
        .add(createParseValue("d", 10));

    public static Stream<Arguments> joinExpressions() {
        return Stream.of(
            // Join of constant expressions
            arguments(join(con(4)), new int[]{4}),
            arguments(join(con(4), con(2)), new int[]{2, 4}),
            arguments(join(con(4), con(2), con(8)), new int[]{8, 2, 4}),

            // Join of expressions resulting into NOT_A_VALUE.
            arguments(join(div(con(1), con(0))), new int[]{-1}),
            arguments(join(con(1), div(con(1), con(0))), new int[]{-1, 1}),
            arguments(join(div(con(1), con(0)), con(1)), new int[]{1, -1}),

            // Join of single valueExpressions of different sizes.
            arguments(join(ref("a")), new int[0]),
            arguments(join(ref("b")), new int[]{6, 5}),
            arguments(join(ref("c")), new int[]{9, 8, 7}),
            arguments(join(ref("d")), new int[]{10}),

            // Join of multiple value expressions of different sizes.
            arguments(join(ref("a"), ref("b")), new int[]{6, 5}),
            arguments(join(ref("b"), ref("c")), new int[]{9, 8, 7, 6, 5}),
            arguments(join(ref("c"), ref("d")), new int[]{10, 9, 8, 7}),
            arguments(join(ref("b"), ref("d")), new int[]{10, 6, 5}),
            arguments(join(ref("b"), ref("c"), ref("d")), new int[]{10, 9, 8, 7, 6, 5}),
            arguments(join(ref("a"), ref("b"), ref("c"), ref("d")), new int[]{10, 9, 8, 7, 6, 5}),
            arguments(join(ref("d"), ref("c"), ref("b"), ref("a")), new int[]{6, 5, 9, 8, 7, 10}),
            arguments(join(ref("d"), ref("a"), ref("c"), ref("b")), new int[]{6, 5, 9, 8, 7, 10}),

            arguments(join(), new int[0])
        );
    }

    @ParameterizedTest
    @MethodSource("joinExpressions")
    public void joinWithParseState(final ValueExpression expression, final int[] expected) {
        ImmutableList<Value> result = expression.eval(PARSE_STATE, enc());
        assertEquals(expected.length, result.size);
        assertResult(expected, result);
    }

    private static void assertResult(final int[] expected, ImmutableList<Value> result) {
        for (final int value : expected) {
            if (value == -1) {
                assertEquals(NOT_A_VALUE, result.head);
            }
            else {
                assertEquals(value, result.head.asNumeric().intValueExact());
            }
            result = result.tail;
        }
    }

    @Test
    public void joinNull() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> join(null).eval(EMPTY_PARSE_STATE, enc()));
        assertEquals("Argument expression may not be null.", e.getMessage());
    }

    @Test
    public void joinAnyNull() {
        final IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> join(con(1), null, con(2)).eval(EMPTY_PARSE_STATE, enc()));
        assertEquals("Value in array expression may not be null.", e.getMessage());
    }

}