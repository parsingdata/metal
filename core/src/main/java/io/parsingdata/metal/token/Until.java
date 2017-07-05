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

import static io.parsingdata.metal.Trampoline.complete;
import static io.parsingdata.metal.Trampoline.intermediate;
import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.Util.success;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.Trampoline;
import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.encoding.Encoding;

public class Until extends Token {

    private final Token terminator;

    public Until(final String name, final Token terminator, final Encoding encoding) {
        super(name, encoding);
        this.terminator = checkNotNull(terminator, "terminator");
    }

    @Override
    protected Optional<Environment> parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        return iterate(scope, environment, encoding, 0).computeResult();
    }

    private Trampoline<Optional<Environment>> iterate(final String scope, final Environment environment, final Encoding encoding, final int currentSize) throws IOException {
        return terminator.parse(scope, currentSize == 0 ? environment : environment.add(new ParseValue(name, this, environment.slice(currentSize), encoding)).seek(environment.offset + currentSize), encoding)
            .map(nextEnvironment -> complete(() -> success(nextEnvironment)))
            .orElseGet(() -> intermediate(() -> iterate(scope, environment, encoding, currentSize + 1)));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + terminator + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(terminator, ((Until)obj).terminator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), terminator);
    }

}
