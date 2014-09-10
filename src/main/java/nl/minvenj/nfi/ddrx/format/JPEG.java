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

import static nl.minvenj.nfi.ddrx.Shorthand.and;
import static nl.minvenj.nfi.ddrx.Shorthand.cho;
import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.def;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.gtNum;
import static nl.minvenj.nfi.ddrx.Shorthand.ltNum;
import static nl.minvenj.nfi.ddrx.Shorthand.not;
import static nl.minvenj.nfi.ddrx.Shorthand.or;
import static nl.minvenj.nfi.ddrx.Shorthand.ref;
import static nl.minvenj.nfi.ddrx.Shorthand.rep;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.Shorthand.str;
import static nl.minvenj.nfi.ddrx.Shorthand.sub;
import nl.minvenj.nfi.ddrx.token.Token;

/*
 * Since nearly every byte of the scan data is individually matched, this
 * approach will lead to stack overflow on files of realistic size.
 */
public class JPEG {

    private static final Token HEADER =
            str("start of image",
            seq(def("marker", con(1), eq(con(0xff))),
                def("identifier", con(1), eq(con(0xd8)))));

    private static final Token FOOTER =
            str("end of image",
            seq(def("marker", con(1), eq(con(0xff))),
                def("identifier", con(1), eq(con(0xd9)))));

    private static final Token SIZED_SEGMENT =
            str("sized segment",
            seq(def("marker", con(1), eq(con(0xff))),
                def("identifier", con(1), or(ltNum(con(0xd8)), gtNum(con(0xda)))),
                def("length", con(2)),
                def("payload", sub(ref("length"), con(2)))));

    private static final Token SCAN_SEGMENT =
            str("scan segment",
            seq(def("marker", con(1), eq(con(0xff))),
                def("identifier", con(1), eq(con(0xda))),
                def("length", con(2)),
                def("payload", sub(ref("length"), con(2))),
                rep(cho(def("scandata", con(1), not(eq(con(0xff)))),
                        def("escape", con(2), or(eq(con(0xff00)), and(gtNum(con(0xffcf)), ltNum(con(0xffd8)))))))));

    public static final Token FORMAT =
            str("JPEG",
            seq(HEADER,
                rep(cho(SIZED_SEGMENT, SCAN_SEGMENT)),
                FOOTER));

}
