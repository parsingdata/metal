package io.parsingdata.metal.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.token.Token;

public class DataExpressionSourceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public ParseValue setupValue() throws IOException {
        final ParseResult result = setupResult();
        assertTrue(result.succeeded);
        return getValue(result.environment.order, "b");
    }

    private ParseResult setupResult() throws IOException {
        final Token token =
            seq(def("a", con(4)),
                tie(def("b", con(2)), ref("a")));
        return token.parse(stream(1, 2, 3, 4), enc());
    }

    @Test
    public void createSliceFromParseValue() throws IOException {
        final ParseValue value = setupValue();
        assertEquals(2, value.slice.source.slice(2, 4).size);
        assertEquals(0, value.slice.source.slice(4, 4).size);
    }

    @Test
    public void indexOutOfBounds() throws IOException {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("ValueExpression dataExpression yields incorrect amount of results.");
        final ParseResult result = setupResult();
        final DataExpressionSource source = new DataExpressionSource(ref("a"), 1, result.environment, enc());
        source.getData(0, 4);
    }

}
