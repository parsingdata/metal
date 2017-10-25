package io.parsingdata.metal.data;

import static java.math.BigInteger.ZERO;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.expression.value.BytesTest.EMPTY_PARSE_STATE;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.token.Token;

public class DataExpressionSourceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public ParseValue setupValue() {
        final Optional<ParseState> result = setupResult();
        assertTrue(result.isPresent());
        return getValue(result.get().order, "b");
    }

    private Optional<ParseState> setupResult() {
        final Token token =
            seq(def("a", con(4)),
                tie(def("b", con(2)), ref("a")));
        return token.parse(stream(1, 2, 3, 4), enc());
    }

    @Test
    public void createSliceFromParseValue() {
        final ParseValue value = setupValue();
        assertTrue(value.slice.source.isAvailable(ZERO, BigInteger.valueOf(4)));
        assertFalse(value.slice.source.isAvailable(ZERO, BigInteger.valueOf(5)));
    }

    @Test
    public void indexOutOfBounds() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("ValueExpression dataExpression yields 1 result(s) (expected at least 2).");
        final Optional<ParseState> result = setupResult();
        final DataExpressionSource source = new DataExpressionSource(ref("a"), 1, result.get(), enc());
        source.getData(ZERO, BigInteger.valueOf(4));
    }

    @Test
    public void emptyValue() {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("ValueExpression dataExpression yields empty Value at index 0.");
        new DataExpressionSource(div(con(1), con(0)), 0, EMPTY_PARSE_STATE, enc()).isAvailable(ZERO, ZERO);
    }

}
