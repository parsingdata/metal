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

import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.def;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.ref;
import static nl.minvenj.nfi.ddrx.Shorthand.rep;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import nl.minvenj.nfi.ddrx.token.Token;

public class ZIP {

    private static final Token LOCAL_FILE = seq(def("filesignature", con(4), eq(con(0x504b0304))),
                                            seq(def("extractversion", con(2)),
                                            seq(def("bitflag", con(2)),
                                            seq(def("compressionmethod", con(2)),
                                            seq(def("lastmodtime", con(2)),
                                            seq(def("lastmoddate", con(2)),
                                            seq(def("filecrc32", con(4)),
                                            seq(def("compressedsize", con(4)),
                                            seq(def("uncompressedsize", con(4)),
                                            seq(def("filenamesize", con(2)),
                                            seq(def("extrafieldsize", con(2)),
                                            seq(def("filename", ref("filenamesize")),
                                            seq(def("extrafield", ref("extrafieldsize")),
                                                def("compresseddata", ref("compressedsize")))))))))))))));
    private static final Token FILES = rep(LOCAL_FILE);

    private static final Token DIR_ENTRY = seq(def("dirsignature", con(4), eq(con(0x504b0102))),
                                           seq(def("makeversion", con(2)),
                                           seq(def("extractversion", con(2)),
                                           seq(def("bitflag", con(2)),
                                           seq(def("compressionmethod", con(2)),
                                           seq(def("lastmodtime", con(2)),
                                           seq(def("lastmoddate", con(2)),
                                           seq(def("dircrc32", con(4)),
                                           seq(def("compressedsize", con(4)),
                                           seq(def("uncompressedsize", con(4)),
                                           seq(def("filenamesize", con(2)),
                                           seq(def("extrafieldsize", con(2)),
                                           seq(def("filecommentsize", con(2)),
                                           seq(def("filedisk", con(2)),
                                           seq(def("intfileattr", con(2)),
                                           seq(def("extfileattr", con(4)),
                                           seq(def("offset", con(4)),
                                           seq(def("filename", ref("filenamesize")),
                                           seq(def("extrafield", ref("extrafieldsize")),
                                               def("filecomment", ref("filecommentsize")))))))))))))))))))));
    private static final Token DIRS = rep(DIR_ENTRY);

    private static final Token END_OF_DIR = seq(def("endofdirsignature", con(4), eq(con(0x504b0506))),
                                            seq(def("disknumber", con(2)),
                                            seq(def("dirdisk", con(2)),
                                            seq(def("numlocaldirs", con(2)),
                                            seq(def("numtotaldirs", con(2)),
                                            seq(def("dirsize", con(4)),
                                            seq(def("diroffset", con(4)),
                                            seq(def("commentsize", con(2)),
                                                def("comment", ref("commentsize"))))))))));

    public static final Token FORMAT = seq(FILES,
                                       seq(DIRS,
                                           END_OF_DIR));

}
