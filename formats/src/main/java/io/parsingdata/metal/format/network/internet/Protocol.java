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
 * Constants class containing the IP protocol numbers.
 *
 * @author Netherlands Forensic Institute.
 */
public final class Protocol {

    /** Internet Control Message Protocol. */
    public static final int ICMP = 1;

    /** Internet Group Management Protocol. */
    public static final int IGMP = 2;

    /** IP in IP, an IP tunneling protocol that encapsulates one IP packet in another IP packet. */
    public static final int IP_IN_IP = 4;

    /** Transmission Control Protocol. */
    public static final int TCP = 6;

    /** User Datagram Protocol. */
    public static final int UDP = 17;

    /** Generic Routing Encapsulation. */
    public static final int GRE = 47;

    /** Internet Control Message Protocol for IPv6. */
    public static final int ICMPV6 = 58;

    private Protocol() {
    }
}
