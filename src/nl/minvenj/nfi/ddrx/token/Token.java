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

package nl.minvenj.nfi.ddrx.token;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.encoding.Encoding;

public abstract class Token {

    public final static String DEFAULT_NAME = "W";

    private final Encoding _enc;

    protected Token(Encoding enc) {
        _enc = enc;
    }

    public boolean parse(String name, Environment env, Encoding enc) {
        return _enc == null ? parseImpl(name, env, enc) : parseImpl(name, env, _enc);
    }

    public boolean parse(Environment env, Encoding enc) {
        return parse(DEFAULT_NAME, env, enc);
    }

    protected abstract boolean parseImpl(String name, Environment env, Encoding enc);

}
