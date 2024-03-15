/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

import java.util.Optional;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseReference;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.Source;
import io.parsingdata.metal.encoding.Encoding;

/**
 * Base class for {@link Token} implementations that can potentially cause a cycle if used in combination
 * with {@link Sub}. All {@link Token}s of this type can encapsulate a {@link Sub} which may lead to a cycle.
 * In general, all non-terminal {@link Token}s are of this type except {@link TokenRef} (which is substituted
 * by a {@link Token} it references and so cycle detection happens on its referenced {@link Token} instead) and
 * {@link Tie} (which is a non-terminal {@link Token} but since it uses a different {@link Source} it cannot
 * directly create a cycle).
 */
public abstract class CycleToken extends Token {

    protected CycleToken(String name, Encoding encoding) {
        super(name, encoding);
    }

    /**
     * Parse this {@link Token}. Adds itself to the list of references maintained in the {@link Environment}'s
     * {@link ParseState} object to quickly allow detection of potential cycles.
     *
     * @param environment the environment to apply the parse to
     * @return a non-empty {@code Optional<ParseState>} if successful, otherwise {@code Optional.empty()}
     */
    @Override
    public Optional<ParseState> parse(Environment environment) {
        return super.parse(
            environment.addCycleReference(
                    new ParseReference(environment.parseState.offset, environment.parseState.source, this.getCanonical(environment.parseState))));
    }
}
