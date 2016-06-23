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

public abstract class BinaryValueExpression implements ValueExpression {

    private final ValueExpression _lop;
    private final ValueExpression _rop;

    public BinaryValueExpression(final ValueExpression lop, final ValueExpression rop) {
        _lop = checkNotNull(lop, "lop");
        _rop = checkNotNull(rop, "rop");
    }

    @Override
    public OptionalValueList eval(final Environment env, final Encoding enc) {
        final OptionalValueList lvl = _lop.eval(env, enc);
        if (lvl.isEmpty()) { return lvl; }
        final OptionalValueList rvl = _rop.eval(env, enc);
        if (rvl.isEmpty()) { return rvl; }
        return evalLists(lvl, rvl, env, enc);
    }

    private OptionalValueList evalLists(final OptionalValueList lvl, final OptionalValueList rvl, final Environment env, final Encoding enc) {
        if (lvl.isEmpty()) { return lvl; }
        return evalLists(lvl.tail, rvl, env, enc).add(evalLeftHead(lvl.head, rvl, env, enc));
    }

    private OptionalValueList evalLeftHead(final OptionalValue lhead, final OptionalValueList rvl, final Environment env, final Encoding enc) {
        if (rvl.isEmpty()) { return rvl; }
        return evalLeftHead(lhead, rvl.tail, env, enc).add(evalValues(lhead, rvl.head, env, enc));
    }

    private OptionalValue evalValues(final OptionalValue lhead, final OptionalValue rhead, final Environment env, final Encoding enc) {
        if (!lhead.isPresent() || !rhead.isPresent()) { return lhead; }
        return eval(lhead.get(), rhead.get(), env, enc);
    }

    public abstract OptionalValue eval(final Value lv, final Value rv, final Environment env, final Encoding enc);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _lop + "," + _rop + ")";
    }

}
