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
import java.util.Objects;
import java.util.Optional;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseReference;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.Source;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link Token} that specifies a token to be parsed at a specific location
 * in the input.
 * <p>
 * A Sub consists of a <code>token</code> (a {@link Token}) and an
 * <code>address</code> (a {@link ValueExpression}). First
 * <code>address</code> is evaluated. Then each resulting value is used as a
 * location in the input to parse <code>token</code> at. Sub succeeds if all
 * parses of <code>token</code> at all locations succeed. Sub fails if
 * <code>address</code> evaluates to a list of locations that is either empty
 * or contains an invalid value.
 *
 * @see ValueExpression
 */
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
        final ImmutableList<Optional<Value>> addresses = address.eval(environment.order, encoding);
        if (addresses.isEmpty()) {
            return failure(environment);
        }
        final ParseResult result = iterate(scope, addresses, environment.addBranch(this), encoding);
        if (result.succeeded) {
            return success(result.environment.closeBranch().seek(environment.offset));
        }
        return failure(environment);
    }

    private ParseResult iterate(final String scope, final ImmutableList<Optional<Value>> addresses, final Environment environment, final Encoding encoding) throws IOException {
        if (!addresses.head.isPresent()) {
            return failure(environment);
        }
        final long offset = addresses.head.get().asNumeric().longValue();
        final Source source = environment.source;
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
        return token.parse(scope, environment.seek(offset), encoding);
    }

    @Override
    public boolean isLocal() { return false; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + ", " + address + ")";
    }

    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj)
            && Objects.equals(token, ((Sub)obj).token)
            && Objects.equals(address, ((Sub)obj).address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token, address);
    }

}
