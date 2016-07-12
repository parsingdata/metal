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

package io.parsingdata.metal.expression.value;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.OptionalValueList;
import io.parsingdata.metal.encoding.Encoding;

import static io.parsingdata.metal.Util.checkNotNull;

/**
 * Expression for the 'elvis operator': <pre>?:</pre>.
 * <p>
 * Example:
 *
 * <pre>
 *   elvis(ref("foo"), ref("bar"))
 * </pre>
 *
 * If <code>ref("foo")</code> can be successfully evaluated, this elvis-expression
 * evaluates to that value, else it evaluates to the value of <code>ref("bar")</code>.
 */
public class Elvis implements ValueExpression {
    private final ValueExpression lop;
    private final ValueExpression rop;

    public Elvis(final ValueExpression lop, final ValueExpression rop) {
        this.lop = checkNotNull(lop, "lop");
        this.rop = checkNotNull(rop, "rop");
    }

    @Override
    public OptionalValueList eval(final Environment env, final Encoding enc) {
        return eval(lop.eval(env, enc), rop.eval(env, enc));
    }

    private OptionalValueList eval(final OptionalValueList llist, final OptionalValueList rlist) {
        if (llist.isEmpty()) { return rlist; }
        if (rlist.isEmpty()) { return llist; }
        return eval(llist.tail, rlist.tail).add(llist.head.isPresent() ? llist.head : rlist.head);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + lop + "," + rop + ")";
    }
}
