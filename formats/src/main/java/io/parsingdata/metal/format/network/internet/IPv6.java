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

import static io.parsingdata.metal.Shorthand.add;
import static io.parsingdata.metal.Shorthand.and;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.currentOffset;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.mul;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.offset;
import static io.parsingdata.metal.Shorthand.or;
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.self;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.shr;
import static io.parsingdata.metal.Shorthand.str;
import static io.parsingdata.metal.Shorthand.sub;
import static io.parsingdata.metal.Shorthand.whl;
import static io.parsingdata.metal.util.CompoundExpression.expFalse;

import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.format.network.transport.TCP;
import io.parsingdata.metal.format.network.transport.UDP;
import io.parsingdata.metal.token.Token;

/**
 * IPv6 format definition.
 *
 * @author Netherlands Forensic Institute.
 */
public final class IPv6 {

    /** No next header, used to indicate nothing follows this header. */
    public static final Expression NO_NEXT_HEADER = eqNum(ref("nextheader"), con(59));

    /** Total data size in octets. */
    public static final ValueExpression PAYLOAD_SIZE = ref("payloadlength");

    /** Offset of the start of the IPv6 payload. */
    public static final ValueExpression PAYLOAD_OFFSET = add(offset("destinationaddress"), con(16));

    /** IPv6 header format definition. **/
    public static final Token HEADER = seq(
                                           def("vertraflo", 4, eqNum(shr(self, con(28)), con(6))), // version (= 6), traffic class, flow label
                                           def("payloadlength", 2),
                                           def("nextheader", 1), // holds the protocol number for the next extension header
                                           def("hoplimit", 1),
                                           def("sourceaddress", 16),
                                           def("destinationaddress", 16),
                                           whl(seq(
                                                   pre(
                                                       def("fragmentheader", 8),
                                                       eqNum(ref("nextheader"), con(ExtensionHeaderTypes.FRAGMENT.value()))),
                                                   pre(
                                                       seq(
                                                           def("nextheader", 1),
                                                           def("ahlen", 1),
                                                           def("ahdata", mul(add(ref("ahlen"), con(2)), con(4)))),
                                                       eqNum(ref("nextheader"), con(ExtensionHeaderTypes.AUTHENTICATION.value()))),
                                                   pre(
                                                       seq(
                                                           def("nextheader", 1),
                                                           def("hdrextlen", 1),
                                                           def("hdrextdata", add(mul(ref("hdrextlen"), con(8)), con(6)))), // header length in 8 bytes, not including first 8 (6 + 2) bytes
                                                       not(isAnyOf(ref("nextheader"), con(ExtensionHeaderTypes.FRAGMENT.value()), con(ExtensionHeaderTypes.AUTHENTICATION.value()))))),
                                               and(isExtensionHeader(ref("nextheader")), not(NO_NEXT_HEADER))));

    /** IPv6 data format definition. **/
    public static final Token DATA = seq(
                                         pre(IPv4.FORMAT, eqNum(ref("nextheader"), con(Protocol.IP_IN_IP))),
                                         pre(UDP.FORMAT, eqNum(ref("nextheader"), con(Protocol.UDP))),
                                         pre(TCP.FORMAT, eqNum(ref("nextheader"), con(Protocol.TCP))),
                                         pre(
                                             def("ipv6data", sub(sub(PAYLOAD_SIZE, sub(currentOffset, offset("destinationaddress"))), con(16))),
                                                 not(isAnyOf(ref("nextheader"), con(Protocol.IP_IN_IP), con(Protocol.UDP), con(Protocol.TCP)))));

    /** IPv6 format definition. **/
    public static final Token FORMAT = str("IPV6PACKET", seq(HEADER, DATA));

    private IPv6() {
    }

    /**
     * Check if given value is equal to any of one or more values.
     *
     * @param value the value to check for
     * @param values the values to compare to
     * @return an expression which evaluates to true if the value is equal to any of the given values
     */
    private static Expression isAnyOf(final ValueExpression value, final ValueExpression... values) {
        Expression expression = expFalse();
        for (final ValueExpression val : values) {
            expression = or(expression, eqNum(value, val));
        }
        return expression;
    }

    /**
     * Used to check if given value represents an Extension Header field value.
     *
     * @param value the value to check
     * @return an expression which evaluates to true if the value represents an Extension Header field value
     */
    private static Expression isExtensionHeader(final ValueExpression value) {
        final ValueExpression nextHeader = ref("nextheader");
        Expression expression = expFalse();
        for (final ExtensionHeaderTypes headerType : ExtensionHeaderTypes.values()) {
            expression = or(expression, eqNum(nextHeader, con(headerType.value())));
        }
        return expression;
    }
}
