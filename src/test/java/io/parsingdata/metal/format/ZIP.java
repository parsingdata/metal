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

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.expTrue;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.self;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.str;
import static io.parsingdata.metal.format.Callback.crc32;
import static io.parsingdata.metal.format.Callback.inflate;

import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.token.Token;

/*
 * Implements limited subset of ZIP file format:
 * - Only single-part files.
 * - Only Store and Deflate compression schemes.
 */
public class ZIP {

    private static Token localFileBody(final int cm, final Expression crc, final Expression cs, final Expression usp) {
        return
        seq(def("filesignature", con(4), eq(con(0x50, 0x4b, 0x03, 0x04))),
            def("extractversion", con(2)),
            def("bitflag", con(2)),
            def("compressionmethod", con(2), eqNum(con(cm))),
            def("lastmodtime", con(2)),
            def("lastmoddate", con(2)),
            def("crc32", con(4), crc),
            def("compressedsize", con(4), cs),
            def("uncompressedsize", con(4), usp),
            def("filenamesize", con(2)),
            def("extrafieldsize", con(2)),
            def("filename", ref("filenamesize")),
            def("extrafield", ref("extrafieldsize")));
    }

    private static final Token LOCAL_DEFLATED_FILE =
        str("file",
            seq(localFileBody(8, expTrue(), expTrue(), expTrue()),
                def("compresseddata", ref("compressedsize"), eqNum(crc32(inflate(self)), ref("crc32")))));

    private static final Token LOCAL_EMPTY_FILE =
        str("file",
            localFileBody(0, eqNum(con(0)), eqNum(con(0)), eqNum(con(0))));

    private static final Token LOCAL_STORED_FILE =
        str("file",
            seq(localFileBody(0, expTrue(), expTrue(), eq(ref("compressedsize"))),
                def("compresseddata", ref("compressedsize"), eqNum(crc32(self), ref("crc32")))));

    private static final Token FILES =
        rep(cho(LOCAL_DEFLATED_FILE,
                cho(LOCAL_EMPTY_FILE,
                    LOCAL_STORED_FILE)));

    private static final Token DIR_ENTRY =
        str("dir",
            seq(def("dirsignature", con(4), eq(con(0x50, 0x4b, 0x01, 0x02))),
                def("makeversion", con(2)),
                def("extractversion", con(2)),
                def("bitflag", con(2)),
                def("compressionmethod", con(2)),
                def("lastmodtime", con(2)),
                def("lastmoddate", con(2)),
                def("crc32", con(4)),
                def("compressedsize", con(4)),
                def("uncompressedsize", con(4)),
                def("filenamesize", con(2)),
                def("extrafieldsize", con(2)),
                def("filecommentsize", con(2)),
                def("filedisk", con(2), eqNum(con(0))),
                def("intfileattr", con(2)),
                def("extfileattr", con(4)),
                def("offset", con(4)),
                def("filename", ref("filenamesize")),
                def("extrafield", ref("extrafieldsize")),
                def("filecomment", ref("filecommentsize"))));

    private static final Token DIRS =
        rep(DIR_ENTRY);

    private static final Token END_OF_DIR =
        str("endofdir",
            seq(def("endofdirsignature", con(4), eq(con(0x50, 0x4b, 0x05, 0x06))),
                def("disknumber", con(2), eqNum(con(0))),
                def("dirdisk", con(2), eqNum(con(0))),
                def("numlocaldirs", con(2)),
                def("numtotaldirs", con(2), eq(ref("numlocaldirs"))),
                def("dirsize", con(4)),
                def("diroffset", con(4)),
                def("commentsize", con(2)),
                def("comment", ref("commentsize"))));

    public static final Token FORMAT =
        str("ZIP",
            seq(FILES,
                DIRS,
                END_OF_DIR), new Encoding(ByteOrder.LITTLE_ENDIAN));

}
