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

package nl.minvenj.nfi.metal.token;

import java.io.IOException;

import nl.minvenj.nfi.metal.data.Environment;
import nl.minvenj.nfi.metal.data.ParseResult;
import nl.minvenj.nfi.metal.encoding.Encoding;

public class Seq extends Token {

    private final Token _l;
    private final Token _r;

    public Seq(final Token l, final Token r, final Encoding enc) {
        super(enc);
        if (l == null) { throw new IllegalArgumentException("Argument l may not be null."); }
        _l = l;
        if (r == null) { throw new IllegalArgumentException("Argument r may not be null."); }
        _r = r;
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        final ParseResult lRes = _l.parse(scope, env, enc);
        if (!lRes.succeeded()) { return lRes; }
        return _r.parse(scope, lRes.getEnvironment(), enc);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + _l + "," + _r + ")";
    }

}
