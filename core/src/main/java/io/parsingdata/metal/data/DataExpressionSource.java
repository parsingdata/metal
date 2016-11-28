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

package io.parsingdata.metal.data;

import static io.parsingdata.metal.Util.checkNotNull;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.ValueExpression;

public class DataExpressionSource implements Source {

    public final ValueExpression dataExpression;
    public final Environment environment;
    public final Encoding encoding;

    public DataExpressionSource(final ValueExpression dataExpression, final Environment environment, final Encoding encoding) {
        this.dataExpression = checkNotNull(dataExpression, "dataExpression");
        this.environment = checkNotNull(environment, "environment");
        this.encoding = checkNotNull(encoding, "encoding");
    }

    @Override
    public Slice create(final long offset, final int size) {
        return new DataExpressionSlice(dataExpression, environment, encoding, offset, size);
    }

}
