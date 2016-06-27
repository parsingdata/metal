/**
 * Copyright 2016 National Cyber Security Centre, Netherlands Forensic Institute
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
package io.parsingdata.metal.format.network.link;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.str;

import io.parsingdata.metal.format.network.internet.IPv4;
import io.parsingdata.metal.format.network.internet.IPv6;
import io.parsingdata.metal.token.Token;

/**
 * Ethernet II format definition.
 *
 * @author Netherlands Forensic Institute.
 */
public final class Ethernet2Frame {

    /** Ethernet Frame II header format definition. */
    public static final Token HEADER = str("macheader",
                                           seq(
                                               def("macdestination", 6),
                                               def("macsource", 6),
                                               def("ethertype", 2)));

    /** Ethernet Frame II data format definition. */
    public static final Token DATA = str("payload", seq(
                                                        pre(IPv4.FORMAT, eqNum(ref("ethertype"), con(0x0800))),
                                                        pre(IPv6.FORMAT, eqNum(ref("ethertype"), con(0x86DD)))));

    /** Ethernet Frame II format definition. */
    public static final Token FORMAT = str("ETHERNETFRAME",
                                           seq(HEADER, DATA));

    private Ethernet2Frame() {
    }
}
