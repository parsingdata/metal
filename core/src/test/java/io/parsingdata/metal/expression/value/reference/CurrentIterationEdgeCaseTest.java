package io.parsingdata.metal.expression.value.reference;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.expression.value.Value;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Optional;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.div;
import static io.parsingdata.metal.Shorthand.exp;
import static io.parsingdata.metal.Shorthand.iteration;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class CurrentIterationEdgeCaseTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    ParseState parseState;

    @Before
    public void before() throws IOException {
        parseState = rep(any("a")).parse(env(stream(1, 2, 3))).get();
    }

    @Test
    public void multiLevel() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Level must evaluate to a single non-empty value.");
        iteration(ref("a")).eval(parseState, enc());
    }

    @Test
    public void emptyLevel() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Level must evaluate to a single non-empty value.");
        iteration(last(ref("b"))).eval(parseState, enc());
    }

    @Test
    public void emptyLevelHead() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Level must evaluate to a single non-empty value.");
        iteration(div(con(1), con(0))).eval(parseState, enc());
    }

    @Test
    public void negativeLevel() {
        ImmutableList<Optional<Value>> value = iteration(con(-1)).eval(parseState, enc());
        assertThat(value.head.isPresent(), is(equalTo(false)));
    }
}