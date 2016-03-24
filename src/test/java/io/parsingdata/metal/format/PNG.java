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

package io.parsingdata.metal.format;

import static io.parsingdata.metal.Shorthand.cat;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.str;
import static io.parsingdata.metal.format.Callback.crc32;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;

public class PNG {

    private static final Token HEADER =
            str("signature",
            seq(def("highbit", con(1), eq(con(0x89))),
                def("PNG", con(3), eq(con("PNG"))),
                def("controlchars", con(4), eq(con(0x0d, 0x0a, 0x1a, 0x0a)))));

    private static final Token FOOTER =
            str("footer",
            seq(def("footerlength", con(4), eqNum(con(0))),
                def("footertype", con(4), eq(con("IEND"))),
                def("footercrc32", con(4), eq(con(0xae, 0x42, 0x60, 0x82)))));

    private static final Token STRUCT =
            str("chunk",
            seq(def("length", con(4)),
                def("chunktype", con(4), not(eq(con("IEND")))),
                def("chunkdata", ref("length")),
                def("crc32", con(4), eq(crc32(cat(ref("chunktype"), ref("chunkdata")))))));

    public static final Token FORMAT =
            str("PNG",
            seq(HEADER,
                rep(STRUCT),
                FOOTER), new Encoding());

}
