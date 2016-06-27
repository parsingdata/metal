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

import static io.parsingdata.metal.CustomExpression.gtEqNum;
import static io.parsingdata.metal.Shorthand.and;
import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.gtNum;
import static io.parsingdata.metal.Shorthand.mul;
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.self;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.shr;
import static io.parsingdata.metal.Shorthand.str;
import static io.parsingdata.metal.Shorthand.sub;

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.BinaryValueExpression;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.format.network.transport.TCP;
import io.parsingdata.metal.format.network.transport.UDP;
import io.parsingdata.metal.token.Token;

/**
 * IPv4 format definition.
 *
 * @author Netherlands Forensic Institute.
 */
public final class IPv4 {

    /** Header size in octets. */
    public static final BinaryValueExpression HEADER_SIZE = mul(and(ref("versionihl"), con(0x0F)), con(4));

    /** Total packet size in octets. */
    public static final ValueExpression TOTAL_SIZE = ref("iplength");

    /** IPv4 header format definition. **/
    public static final Token HEADER = seq(
                                           def("versionihl", 1, and(eqNum(shr(self, con(4)), con(4)), gtEqNum(and(ref("versionihl"), con(0x0F)), con(5)))), // version + IHL; 0x4[5-F] = IPv4
                                           def("dscpecn", 1), // DSCP + ECN
                                           def("iplength", 2, gtEqNum(con(20))), // minimal 20 bytes packet
                                           def("identification", 2),
                                           def("flfr", 2), // flags + fragment offset
                                           def("timetolive", 1),
                                           def("protocol", 1),
                                           def("headerchecksum", 2),
                                           def("ipsource", 4),
                                           def("ipdestination", 4),
                                           pre(
                                               def("options",
                                                   sub(
                                                       HEADER_SIZE,
                                                       con(20))),
                                               gtNum(
                                                     HEADER_SIZE,
                                                     con(20)))); // header size > 5 * 4 bytes for options

    /** Definition of known protocol is stated in IPv4 header. */
    public static final Token KNOWN_PROTOCOL = new Token(new Encoding()) {
        // this anonymous token is needed because of the circular references IPv4 -> ICMP -> IPv4
        // it breaks because the circular reference is in the statically initialized
        // format definition, whereas the class here gets lazily loaded
        @Override
        protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
            if (eqNum(ref("protocol"), con(Protocol.ICMP)).eval(env, enc)) {
                return ICMP.FORMAT.parse(scope, env, enc);
            }
            else if (eqNum(ref("protocol"), con(Protocol.UDP)).eval(env, enc)) {
                return UDP.FORMAT.parse(scope, env, enc);
            }
            else if (eqNum(ref("protocol"), con(Protocol.TCP)).eval(env, enc)) {
                return TCP.FORMAT.parse(scope, env, enc);
            }
            return new ParseResult(false, env);
        }
    };

    /** IPv4 data format definition. **/
    public static final Token DATA = cho(KNOWN_PROTOCOL, def("ipv4data", sub(TOTAL_SIZE, HEADER_SIZE)));

    /** IPv4 format definition. **/
    public static final Token FORMAT = str("IPV4PACKET", seq(HEADER, DATA));

    private IPv4() {
    }
}