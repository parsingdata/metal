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

import static io.parsingdata.metal.Shorthand.and;
import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.gtNum;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.ltNum;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.or;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;

import io.parsingdata.metal.token.Token;

/*
 * Since nearly every byte of the scan data is individually matched, this
 * approach will lead to stack overflow on files of realistic size.
 */
public final class JPEG {

    public static final String MARKER = "marker";
    public static final String IDENTIFIER = "identifier";
    public static final String LENGTH = "length";
    public static final String PAYLOAD = "payload";

    private JPEG() {}

    private static final Token HEADER =
            seq("start of image",
                def(MARKER, con(1), eq(con(0xff))),
                def(IDENTIFIER, con(1), eq(con(0xd8))));

    private static final Token FOOTER =
            seq("end of image",
                def(MARKER, con(1), eq(con(0xff))),
                def(IDENTIFIER, con(1), eq(con(0xd9))));

    private static final Token SIZED_SEGMENT =
            seq("sized segment",
                def(MARKER, con(1), eq(con(0xff))),
                def(IDENTIFIER, con(1), or(ltNum(con(0xd8)), gtNum(con(0xda)))),
                def(LENGTH, con(2)),
                def(PAYLOAD, sub(last(ref(LENGTH)), con(2))));

    private static final Token SCAN_SEGMENT =
            seq("scan segment",
                def(MARKER, con(1), eq(con(0xff))),
                def(IDENTIFIER, con(1), eq(con(0xda))),
                def(LENGTH, con(2)),
                def(PAYLOAD, last(sub(last(ref(LENGTH)), con(2)))),
                rep(cho(def("scandata", con(1), not(eq(con(0xff)))),
                        def("escape", con(2), or(eq(con(0xff00)), and(gtNum(con(0xffcf)), ltNum(con(0xffd8))))))));

    public static final Token FORMAT =
            seq("JPEG",
                HEADER,
                rep(cho(SIZED_SEGMENT, SCAN_SEGMENT)),
                FOOTER);

}
