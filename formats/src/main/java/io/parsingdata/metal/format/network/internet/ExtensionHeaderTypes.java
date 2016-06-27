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

/**
 * Extension header type constants.
 *
 * @author Netherlands Forensic Institute.
 */
public enum ExtensionHeaderTypes {

    /** Hop-by-Hop options header, carries optional information that must be examined by every node along a packet's delivery path. */
    HOP_BY_HOP(0),

    /** Routing header, used to list intermediate nodes to be "visited". */
    ROUTING(43),

    /** Fragment header, used by an IPv6 source to send a packet larger than would fit in the path MTU to its destination. */
    FRAGMENT(44),

    /** Encapsulating Security Payload header, used to provide a mix of security services in IPv4 and IPv6. */
    ENCAPSULATING_SECURITY(50),

    /** IP Authentication header, used to provide connectionless integrity, data origin authentication and protection against replays. */
    AUTHENTICATION(51),

    /** Destination Options header, used to carry optional information that need be examined only by a packet's destination node(s). */
    DESTINATION_OPTIONS(60),

    /** Mobility header, used by mobile nodes, correspondent nodes and home agents in all messaging related to the creation and management of bindings. */
    MOBILITY_HEADER(135),

    /** Host Identity Protocol header, it allows consenting hosts to securely establish and maintain shared IP-layer state. */
    HOST_IDENTITY_PROTOCOL(139),

    /** Site Multihoming by IPv6 Intermediation, used for failure detection and locator pair exploration functions. */
    SHIM6_PROTOCOL(140);

    private int _value;

    ExtensionHeaderTypes(final int value) {
        _value = value;
    }

    public int value() {
        return _value;
    }
}
