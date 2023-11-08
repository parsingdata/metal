/*
 * Copyright 2013-2021 Netherlands Forensic Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.parsingdata.metal.token;

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.success;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;

public abstract class IterableToken extends CycleToken {

    public final Token token;

    IterableToken(final String name, final Token token, final Encoding encoding) {
        super(name, encoding);
        this.token = checkNotNull(token, "token");
    }

    protected final Optional<ParseState> parse(final Environment environment, final Predicate<Environment> stopCondition, final Function<Environment, Optional<ParseState>> ifIterationFails) {
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
    private Trampoline<Optional<ParseState>> iterate(final Environment environment, final Predicate<Environment> stopCondition, final Function<Environment, Optional<ParseState>> ifIterationFails) {
        if (stopCondition.test(environment)) {
            return complete(() -> success(environment.parseState.closeBranch(this)));
        }
        return token
                .parse(environment)
                .map(nextParseState -> intermediate(() -> iterate(environment.withParseState(nextParseState.iterate()), stopCondition, ifIterationFails)))
                .orElseGet(() -> complete(() -> ifIterationFails.apply(environment)));
    }

    @Override
    public boolean isScopeDelimiter() {
        return true;
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
    public int cachingHashCode() {
        return Objects.hash(super.cachingHashCode(), token);
    }

}
