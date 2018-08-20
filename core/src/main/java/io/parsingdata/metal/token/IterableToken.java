package io.parsingdata.metal.token;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.success;

public abstract class IterableToken extends Token {

    public final Token token;

    IterableToken(String name, final Token token, Encoding encoding) {
        super(name, encoding);
        this.token = checkNotNull(token, "token");
    }

    protected final Optional<ParseState> parse(final Environment environment, final Function<Environment, Boolean> stopCondition, final Function<Environment, Optional<ParseState>> ifIterationFails) {
        return iterate(environment.addBranch(this), stopCondition, ifIterationFails).computeResult();
    }

    /**
     * Iteratively parse iterations of the token, given a stop condition and the logic how to handle a failed parse.
     *
     * @param environment the environment to apply the parse to
     * @param stopCondition a function to determine when to stop the iteration
     * @param ifIterationFails a function to determine how to handle a failed parse
     * @return a trampolined {@code Optional<ParseState>}
     */
    private Trampoline<Optional<ParseState>> iterate(final Environment environment, final Function<Environment, Boolean> stopCondition, final Function<Environment, Optional<ParseState>> ifIterationFails) {
        if (stopCondition.apply(environment)) {
            return complete(() -> success(environment.parseState.closeBranch(this)));
        }
        return token
                .parse(environment)
                .map(nextParseState -> intermediate(() -> iterate(environment.withParseState(nextParseState.iter()), stopCondition, ifIterationFails)))
                .orElseGet(() -> complete(() -> ifIterationFails.apply(environment)));
    }

    @Override
    public boolean isIterable() {
        return true;
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
                && Objects.equals(token, ((IterableToken)obj).token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token);
    }

}
