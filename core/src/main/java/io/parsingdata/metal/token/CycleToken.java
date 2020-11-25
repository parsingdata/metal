/*
 * Copyright 2013-2020 Netherlands Forensic Institute
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
import io.parsingdata.metal.encoding.Encoding;

/**
 * Base class for {@link Token} implementations that can potentially
 * cause a cycle if used in conjunction with {@link Sub}.
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
        return super.parse(environment.withParseState(environment.parseState.withReferences(environment.parseState.references.add(new ParseReference(environment.parseState.offset, environment.parseState.source, this.getCanonical(environment.parseState))))));
    }
}
