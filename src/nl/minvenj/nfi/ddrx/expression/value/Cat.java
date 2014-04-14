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

package nl.minvenj.nfi.ddrx.expression.value;

import nl.minvenj.nfi.ddrx.data.Environment;

public class Cat extends BinaryValueExpression<Value> {

    public Cat(ValueExpression<Value> lop, ValueExpression<Value> rop) {
        super(lop, rop);
    }

    @Override
    public Value eval(final Environment env) {
        return _lop.eval(env).operation(new ValueOperation() {

            @Override
            public Value execute(final byte[] lv) {
                return _rop.eval(env).operation(new ValueOperation() {

                    @Override
                    public Value execute(final byte[] rv) {
                        byte[] res = new byte[lv.length + rv.length];
                        System.arraycopy(lv, 0, res, 0, lv.length);
                        System.arraycopy(rv, 0, res, lv.length, rv.length);
                        return new Value(res, env.getEncoding());
                    }
                });
            }
        });
    }

}
