/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

    public static final String CRC_32 = "crc32";
    public static final String COMPRESSED_SIZE = "compressedsize";
    public static final String FILE_NAME_SIZE = "filenamesize";
    public static final String EXTRA_FIELD_SIZE = "extrafieldsize";
    public static final String EXTRACT_VERSION = "extractversion";
    public static final String BIT_FLAG = "bitflag";
    public static final String COMPRESSION_METHOD = "compressionmethod";
    public static final String LAST_MOD_TIME = "lastmodtime";
    public static final String LAST_MOD_DATE = "lastmoddate";
    public static final String UNCOMPRESSED_SIZE = "uncompressedsize";
    public static final String FILE_NAME = "filename";
    public static final String COMPRESSED_DATA = "compresseddata";
    public static final String DIR_SIGNATURE = "dirsignature";
    public static final String EXTRA_FIELD = "extrafield";

    private ZIP() {}

    private static Token localFileBody(final String name, final int cm, final Expression crc, final Expression cs, final Expression usp) {
        return
        seq(name,
            def("filesignature", con(4), eq(con(0x50, 0x4b, 0x03, 0x04))),
            def(EXTRACT_VERSION, con(2)),
            def(BIT_FLAG, con(2)),
            def(COMPRESSION_METHOD, con(2), eqNum(con(cm))),
            def(LAST_MOD_TIME, con(2)),
            def(LAST_MOD_DATE, con(2)),
            def(CRC_32, con(4), crc),
            def(COMPRESSED_SIZE, con(4), cs),
            def(UNCOMPRESSED_SIZE, con(4), usp),
            def(FILE_NAME_SIZE, con(2)),
            def(EXTRA_FIELD_SIZE, con(2)),
            def(FILE_NAME, last(ref(FILE_NAME_SIZE))),
            def(EXTRA_FIELD, last(ref(EXTRA_FIELD_SIZE))));
    }

    private static final Token LOCAL_DEFLATED_FILE =
        seq("localdeflatedfile",
            localFileBody("", 8, TRUE, TRUE, TRUE),
            def(COMPRESSED_DATA, last(ref(COMPRESSED_SIZE)), eqNum(crc32(inflate(SELF)), last(ref(CRC_32)))));

    private static final Token LOCAL_EMPTY_FILE =
        localFileBody("localemptyfile", 0, eqNum(con(0)), eqNum(con(0)), eqNum(con(0)));

    private static final Token LOCAL_STORED_FILE =
        seq("localstoredfile",
            localFileBody("", 0, TRUE, TRUE, eq(last(ref(COMPRESSED_SIZE)))),
            def(COMPRESSED_DATA, last(ref(COMPRESSED_SIZE)), eqNum(crc32(SELF), last(ref(CRC_32)))));

    private static final Token FILES =
        rep("files",
            cho(LOCAL_DEFLATED_FILE,
                LOCAL_EMPTY_FILE,
                LOCAL_STORED_FILE));

    private static final Token DIR_ENTRY =
        seq("direntry",
            def(DIR_SIGNATURE, con(4), eq(con(0x50, 0x4b, 0x01, 0x02))),
            def("makeversion", con(2)),
            def(EXTRACT_VERSION, con(2)),
            def(BIT_FLAG, con(2)),
            def(COMPRESSION_METHOD, con(2)),
            def(LAST_MOD_TIME, con(2)),
            def(LAST_MOD_DATE, con(2)),
            def(CRC_32, con(4)),
            def(COMPRESSED_SIZE, con(4)),
            def(UNCOMPRESSED_SIZE, con(4)),
            def(FILE_NAME_SIZE, con(2)),
            def(EXTRA_FIELD_SIZE, con(2)),
            def("filecommentsize", con(2)),
            def("filedisk", con(2), eqNum(con(0))),
            def("intfileattr", con(2)),
            def("extfileattr", con(4)),
            def("offset", con(4)),
            def(FILE_NAME, last(ref(FILE_NAME_SIZE))),
            def(EXTRA_FIELD, last(ref(EXTRA_FIELD_SIZE))),
            def("filecomment", last(ref("filecommentsize"))));

    private static final Token DIRS =
        rep("direntries",
            DIR_ENTRY);

    private static final Token END_OF_DIR =
        seq("endofdir",
            def("endofdirsignature", con(4), eq(con(0x50, 0x4b, 0x05, 0x06))),
            def("disknumber", con(2), eqNum(con(0))),
            def("dirdisk", con(2), eqNum(con(0))),
            def("numlocaldirs", con(2), eqNum(count(ref(DIR_SIGNATURE)))),
            def("numtotaldirs", con(2), eqNum(last(ref("numlocaldirs")))),
            def("dirsize", con(4), eqNum(sub(offset(last(ref("endofdirsignature"))), offset(first(ref(DIR_SIGNATURE)))))),
            def("diroffset", con(4), eqNum(offset(first(ref(DIR_SIGNATURE))))),
            def("commentsize", con(2)),
            def("comment", last(ref("commentsize"))));

    public static final Token FORMAT =
            seq("ZIP", new Encoding(ByteOrder.LITTLE_ENDIAN),
                FILES,
                DIRS,
                END_OF_DIR);

}
