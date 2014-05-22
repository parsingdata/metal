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

package nl.minvenj.nfi.ddrx.format;

import static nl.minvenj.nfi.ddrx.Callback.crc32;
import static nl.minvenj.nfi.ddrx.Shorthand.cat;
import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.def;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.eqNum;
import static nl.minvenj.nfi.ddrx.Shorthand.not;
import static nl.minvenj.nfi.ddrx.Shorthand.ref;
import static nl.minvenj.nfi.ddrx.Shorthand.rep;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.Shorthand.str;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;
import nl.minvenj.nfi.ddrx.token.Token;

public class PNG {

    private static final Token HEADER =
            str("signature",
            seq(def("highbit", con(1), eq(con(0x89))),
            seq(def("PNG", con(3), eq(con("PNG"))),
                def("controlchars", con(4), eq(con(0x0d0a1a0a))))));

    private static final Token FOOTER =
            str("footer",
            seq(def("footerlength", con(4), eqNum(con(0))),
            seq(def("footertype", con(4), eq(con("IEND"))),
                def("footercrc32", con(4), eq(con(0xae426082l))))));

    private static final Token STRUCT =
            str("chunk",
            seq(def("length", con(4)),
            seq(def("chunktype", con(4), not(eq(con("IEND")))),
            seq(def("chunkdata", ref("length")),
                def("crc32", con(4), eq(crc32(cat(ref("chunktype"), ref("chunkdata")))))))));

    public static final Token FORMAT =
            str("PNG",
            seq(HEADER,
            seq(rep(STRUCT),
                FOOTER)), enc());

}
