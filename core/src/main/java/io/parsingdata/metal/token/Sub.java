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

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.data.ParseRef;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.selection.ByOffset;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.ValueExpression;

public class Sub extends Token {

    public final Token token;
    public final ValueExpression address;

    public Sub(final String name, final Token token, final ValueExpression address, final Encoding enc) {
        super(name, enc);
        this.token = checkNotNull(token, "token");
        this.address = checkNotNull(address, "address");
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        final OptionalValueList addrs = address.eval(env, enc);
        if (addrs.isEmpty() || addrs.containsEmpty()) { return failure(env); }
        final ParseResult res = iterate(scope, addrs, env.addBranch(this), enc);
        if (res.succeeded) {
            return success(res.environment.closeBranch().seek(env.offset));
        }
        return failure(env);
    }

    private ParseResult iterate(final String scope, final OptionalValueList addrs, final Environment env, final Encoding enc) throws IOException {
        final long ref = addrs.head.get().asNumeric().longValue();
        final ParseResult res = parse(scope, ref, env, enc);
        if (res.succeeded) {
            if (addrs.tail.isEmpty()) {
                return res;
            }
            return iterate(scope, addrs.tail, res.environment, enc);
        }
        return failure(env);
    }

    private ParseResult parse(final String scope, final long ref, final Environment env, final Encoding enc) throws IOException {
        if (ByOffset.hasRootAtRef(env.order, token, ref)) {
            return success(env.add(new ParseRef(ref, token)));
        }
        final ParseResult res = token.parse(scope, env.seek(ref), enc);
        if (res.succeeded) {
            return res;
        }
        return failure(env);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + ", " + address + ")";
    }

}
