package io.parsingdata.metal.token;

import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.io.IOException;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;

public class TokenTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private final Token token = new Token("", null) {
        @Override
        protected Optional<Environment> parseImpl(String scope, Environment environment, Encoding encoding) throws IOException {
            return null;
        }
    };

    @Test
    public void parseNullEnvironment() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument environment may not be null.");
        token.parse("", null, new Encoding());
    }

    @Test
    public void parseNullScope() throws IOException {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument scope may not be null.");
        token.parse(null, stream(), new Encoding());
    }

}
