/*
 * Copyright 2013-2016 Netherlands Forensic Institute
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

import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseState;

public class TokenTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    private final Token token = new Token("", null) {
        @Override
        protected Optional<ParseState> parseImpl(final Environment environment) {
            return Optional.empty();
        }
    };

    @Test
    public void parseNullParseState() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument parseState may not be null.");
        token.parse(env(null, enc()));
    }

    @Test
    public void parseNullScope() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument scope may not be null.");
        token.parse(env(null, stream(), enc()));
    }

}
