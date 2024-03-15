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

package io.parsingdata.metal.format;

import static io.parsingdata.metal.Shorthand.EMPTY;
import static io.parsingdata.metal.Shorthand.and;
import static io.parsingdata.metal.Shorthand.bytes;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.foldLeft;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.or;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rev;
import static io.parsingdata.metal.Shorthand.shl;
import static io.parsingdata.metal.Shorthand.until;

import io.parsingdata.metal.expression.value.SingleValueExpression;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

public final class VarInt {

    private VarInt() {}

    public static Token varInt(final String name) {
        return until(name, con(1), post(EMPTY, eq(and(last(bytes(last(ref(name)))), con(128)), con(0))));
    }

    public static SingleValueExpression decodeVarInt(final ValueExpression expression) {
        return foldLeft(rev(bytes(expression)), VarInt::varIntReducer);
    }

    private static SingleValueExpression varIntReducer(final SingleValueExpression left, final SingleValueExpression right) {
        return or(shl(left, con(7)), and(right, con(127)));
    }

}
