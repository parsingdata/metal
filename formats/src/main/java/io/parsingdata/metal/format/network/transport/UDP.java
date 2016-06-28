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
package io.parsingdata.metal.format.network.transport;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.currentOffset;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.offset;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.str;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.util.CompoundExpression.gtEqNum;

import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.format.network.application.DNS;
import io.parsingdata.metal.token.Token;

/**
 * UDP format definition.
 *
 * @author Netherlands Forensic Institute.
 */
public final class UDP {

    /** Header size in octets. */
    public static final ValueExpression HEADER_SIZE = con(8);

    /** Data size in octets. */
    public static final ValueExpression DATA_SIZE = sub(ref("udplength"), HEADER_SIZE);

    /** UDP header format definition. **/
    public static final Token HEADER = str("udpheader",
                                           seq(
                                               def("sourceport", 2),
                                               def("destinationport", 2),
                                               def("udplength", 2, gtEqNum(con(8))), // minimal 8 bytes packet (= UDP header size)
                                               def("udpchecksum", 2)));

    /** UDP data format definition. **/
    public static final Token DATA = cho(
                                         seq(
                                             DNS.FORMAT,
                                             def("padding", sub(ref("udplength"), sub(currentOffset, offset("sourceport"))), eqNum(con(0)))),
                                         def("udpdata", DATA_SIZE));

    /** UDP format definition. **/
    public static final Token FORMAT = str("UDP", seq(HEADER, DATA));

    private UDP() {
    }
}