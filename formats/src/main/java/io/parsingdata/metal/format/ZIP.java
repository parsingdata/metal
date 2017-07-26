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

import static io.parsingdata.metal.Shorthand.SELF;
import static io.parsingdata.metal.Shorthand.TRUE;
import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.count;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.first;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.offset;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Util.inflate;
import static io.parsingdata.metal.format.Callback.crc32;

import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.token.Token;

/*
 * Implements limited subset of ZIP file format:
 * - Only single-part files.
 * - Only Store and Deflate compression schemes.
 */
public final class ZIP {

    private ZIP() {}

    private static Token localFileBody(final String name, final int cm, final Expression crc, final Expression cs, final Expression usp) {
        return
        seq(name,
            def("filesignature", con(4), eq(con(0x50, 0x4b, 0x03, 0x04))),
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
            def("filename", last(ref("filenamesize"))),
            def("extrafield", last(ref("extrafieldsize"))));
    }

    private static final Token LOCAL_DEFLATED_FILE =
        seq("localdeflatedfile",
            localFileBody("", 8, TRUE, TRUE, TRUE),
            def("compresseddata", last(ref("compressedsize")), eqNum(crc32(inflate(SELF)), last(ref("crc32")))));

    private static final Token LOCAL_EMPTY_FILE =
        localFileBody("localemptyfile", 0, eqNum(con(0)), eqNum(con(0)), eqNum(con(0)));

    private static final Token LOCAL_STORED_FILE =
        seq("localstoredfile",
            localFileBody("", 0, TRUE, TRUE, eq(last(ref("compressedsize")))),
            def("compresseddata", last(ref("compressedsize")), eqNum(crc32(SELF), last(ref("crc32")))));

    private static final Token FILES =
        rep("files",
            cho(LOCAL_DEFLATED_FILE,
                LOCAL_EMPTY_FILE,
                LOCAL_STORED_FILE));

    private static final Token DIR_ENTRY =
        seq("direntry",
            def("dirsignature", con(4), eq(con(0x50, 0x4b, 0x01, 0x02))),
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
            def("filename", last(ref("filenamesize"))),
            def("extrafield", last(ref("extrafieldsize"))),
            def("filecomment", last(ref("filecommentsize"))));

    private static final Token DIRS =
        rep("direntries",
            DIR_ENTRY);

    private static final Token END_OF_DIR =
        seq("endofdir",
            def("endofdirsignature", con(4), eq(con(0x50, 0x4b, 0x05, 0x06))),
            def("disknumber", con(2), eqNum(con(0))),
            def("dirdisk", con(2), eqNum(con(0))),
            def("numlocaldirs", con(2), eqNum(count(ref("dirsignature")))),
            def("numtotaldirs", con(2), eqNum(last(ref("numlocaldirs")))),
            def("dirsize", con(4), eqNum(sub(offset(last(ref("endofdirsignature"))), offset(first(ref("dirsignature")))))),
            def("diroffset", con(4), eqNum(offset(first(ref("dirsignature"))))),
            def("commentsize", con(2)),
            def("comment", last(ref("commentsize"))));

    public static final Token FORMAT =
            seq("ZIP", new Encoding(ByteOrder.LITTLE_ENDIAN),
                FILES,
                DIRS,
                END_OF_DIR);

}
