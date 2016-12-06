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

import static io.parsingdata.metal.Util.checkNotNull;
import static io.parsingdata.metal.data.ParseResult.failure;
import static io.parsingdata.metal.data.ParseResult.success;
import static io.parsingdata.metal.data.selection.ByOffset.hasRootAtOffset;

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseReference;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.Source;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.ValueExpression;

public class Sub extends Token {

    public final Token token;
    public final ValueExpression address;

    public Sub(final String name, final Token token, final ValueExpression address, final Encoding encoding) {
        super(name, encoding);
        this.token = checkNotNull(token, "token");
        this.address = checkNotNull(address, "address");
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final ImmutableList<OptionalValue> addresses = address.eval(environment, encoding);
        if (addresses.isEmpty()) {
            return failure(environment);
        }
        final ParseResult result = iterate(scope, addresses, environment.addBranch(this), encoding);
        if (result.succeeded) {
            return success(result.environment.closeBranch().seek(environment.offset));
        }
        return failure(environment);
    }

    private ParseResult iterate(final String scope, final ImmutableList<OptionalValue> addresses, final Environment environment, final Encoding encoding) throws IOException {
        if (!addresses.head.isPresent()) {
            return failure(environment);
        }
        final long offset = addresses.head.get().asNumeric().longValue();
        final Source source = addresses.head.get().slice.source;
        final ParseResult result = parse(scope, offset, source, environment, encoding);
        if (result.succeeded) {
            if (addresses.tail.isEmpty()) { return result; }
            return iterate(scope, addresses.tail, result.environment, encoding);
        }
        return failure(environment);
    }

    private ParseResult parse(final String scope, final long offset, final Source source, final Environment environment, final Encoding encoding) throws IOException {
        if (hasRootAtOffset(environment.order, token.getCanonical(environment), offset, source)) {
            return success(environment.add(new ParseReference(offset, source, token.getCanonical(environment))));
        }
        final ParseResult result = token.parse(scope, environment.seek(offset), encoding);
        if (result.succeeded) { return result; }
        return failure(environment);
    }

    @Override
    public boolean isLocal() { return false; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + ", " + address + ")";
    }

}
