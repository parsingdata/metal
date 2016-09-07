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

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.data.ParseRef;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.ValueExpression;

import java.io.IOException;

import static io.parsingdata.metal.Util.checkNotNull;

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
        if (addrs.isEmpty() || addrs.containsEmpty()) { return new ParseResult(false, env); }
        final ParseResult res = iterate(scope, addrs, env.addBranch(this), enc);
        if (res.succeeded) {
            return new ParseResult(true, res.environment.closeBranch().seek(env.offset));
        }
        return new ParseResult(false, env);
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
        return new ParseResult(false, env);
    }

    private ParseResult parse(final String scope, final long ref, final Environment env, final Encoding enc) throws IOException {
        if (env.order.hasGraphAtRef(ref)) {
            return new ParseResult(true, env.add(new ParseRef(ref, this)));
        }
        final ParseResult res = token.parse(scope, env.seek(ref), enc);
        if (res.succeeded) {
            return res;
        }
        return new ParseResult(false, env);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + makeNameFragment() + token + ", " + address + ")";
    }

}
