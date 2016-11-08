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
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.ValueByteStream;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

public class Tie extends Token {

    public final Token token;
    public final ValueExpression dataExpression;

    public Tie(final String name, final Token token, final ValueExpression dataExpression, final Encoding encoding) {
        super(name, encoding);
        this.token = checkNotNull(token, "token");
        this.dataExpression = checkNotNull(dataExpression, "dataExpression");
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException {
        final ImmutableList<OptionalValue> dataResult = dataExpression.eval(environment, encoding);
        if (dataResult.size != 1 || !dataResult.head.isPresent()) {
            return failure(environment);
        }
        final Value data = dataResult.head.get();
        final ParseResult result = token.parse(new Environment(environment.addBranch(this).order, new ValueByteStream(data), 0, environment.callbacks), encoding);
        if (result.succeeded) {
            return success(new Environment(result.environment.closeBranch().order, environment.input, environment.offset, environment.callbacks));
        }
        return failure(environment);
    }

}
