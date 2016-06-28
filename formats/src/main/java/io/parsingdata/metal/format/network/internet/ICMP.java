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
package io.parsingdata.metal.format.network.internet;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.str;
import static io.parsingdata.metal.Shorthand.sub;

import io.parsingdata.metal.token.Token;

/**
 * ICMP format definition.
 *
 * @author Netherlands Forensic Institute.
 */
public final class ICMP {

    /** ICMP header format definition. **/
    public static final Token HEADER = seq(
                                           def("icmptype", 1),
                                           def("icmpcode", 1),
                                           def("icmpchecksum", 2),
                                           def("rest", 4));

    /** ICMP data format definition. **/
    public static final Token DATA = cho(
                                         IPv4.FORMAT,
                                         def("icmpdata", sub(sub(IPv4.TOTAL_SIZE, IPv4.HEADER_SIZE), con(8))));

    /** ICMP format definition. **/
    public static final Token FORMAT = str("ICMP", seq(HEADER, DATA));

    private ICMP() {
    }
}
