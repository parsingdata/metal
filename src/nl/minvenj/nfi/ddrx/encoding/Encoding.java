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

package nl.minvenj.nfi.ddrx.encoding;

import java.nio.charset.Charset;

public class Encoding {

    private static final boolean DEFAULT_SIGNED = false;
    private static final Charset DEFAULT_CHARSET = Charset.forName("ISO646-US");

    private final boolean _signed;
    private final Charset _charset;

    public Encoding() {
        this(DEFAULT_SIGNED, DEFAULT_CHARSET);
    }

    public Encoding(boolean signed) {
        this(signed, DEFAULT_CHARSET);
    }

    public Encoding(Charset charset) {
        this(DEFAULT_SIGNED, charset);
    }

    public Encoding(Encoding encoding, boolean signed) {
        this(signed, encoding.getCharset());
    }

    public Encoding(Encoding encoding, Charset charset) {
        this(encoding.isSigned(), charset);
    }

    public Encoding(boolean signed, Charset charset) {
        _signed = signed;
        _charset = charset;
    }

    public boolean isSigned() {
        return _signed;
    }

    public Charset getCharset() {
        return _charset;
    }

}
