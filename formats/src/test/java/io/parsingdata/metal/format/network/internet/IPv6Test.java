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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.format.network.Util.hexStringToBytes;
import static io.parsingdata.metal.format.network.Util.ipStringFromBytes;
import static io.parsingdata.metal.util.Util.parse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseResult;

public class IPv6Test {

    // the expected values are based on the ones given by Wireshark
    // these byte streams are direct copies from Wireshark parses

    @Test
    public void testIPv6() throws IOException {
        final String hexString = "6000000000383afffe800000000000005a6d8ffffe563009ff0200000000000000000000000000018600ab32405807080000000000000000030440c00000001e00000014000000002002183ddba4000000000000000000000101586d8f563009";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv6.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("vertraflo").getValue(), is(equalTo(hexStringToBytes("60000000"))));
        assertThat(values.get("payloadlength").asNumeric().intValue(), is(equalTo(56)));
        assertThat(values.get("nextheader").asNumeric().intValue(), is(equalTo(Protocol.ICMPV6)));
        assertThat(values.get("hoplimit").asNumeric().intValue(), is(equalTo(255)));
        assertThat(values.get("sourceaddress").getValue(), is(equalTo(hexStringToBytes("FE800000000000005A6D8FFFFE563009"))));
        assertThat(values.get("destinationaddress").getValue(), is(equalTo(hexStringToBytes("FF020000000000000000000000000001"))));
    }

    @Test
    public void testUDP() throws IOException {
        final String hexString = "60000000003c1101fe800000000000000a0027fffefe8f95ff02000000000000000000000001000202220223003cad08011008740001000e000100011c39cf88080027fe8f9500060004001700180008000200000019000c27fe8f9500000e1000001518";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv6.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("vertraflo").getValue(), is(equalTo(hexStringToBytes("60000000"))));
        assertThat(values.get("payloadlength").asNumeric().intValue(), is(equalTo(60)));
        assertThat(values.get("nextheader").asNumeric().intValue(), is(equalTo(Protocol.UDP)));
        assertThat(values.get("hoplimit").asNumeric().intValue(), is(equalTo(1)));
        assertThat(values.get("sourceaddress").getValue(), is(equalTo(hexStringToBytes("FE800000000000000A0027FFFEFE8F95"))));
        assertThat(values.get("destinationaddress").getValue(), is(equalTo(hexStringToBytes("FF020000000000000000000000010002"))));
        assertThat(values.get("sourceport").asNumeric().intValue(), is(equalTo(546)));
        assertThat(values.get("destinationport").asNumeric().intValue(), is(equalTo(547)));
        assertThat(values.get("udplength").asNumeric().intValue(), is(equalTo(60)));
        assertThat(values.get("udpchecksum").getValue(), is(equalTo(hexStringToBytes("AD08"))));
    }

    @Test
    public void testTCP() throws IOException {
        final String hexString = "600000000020064020010470e5bfdead49572174e82c48872607f8b0400c0c03000000000000001af9c7001903a088300000000080022000da4700000204058c0103030801010402";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv6.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("vertraflo").getValue(), is(equalTo(hexStringToBytes("60000000"))));
        assertThat(values.get("payloadlength").asNumeric().intValue(), is(equalTo(32)));
        assertThat(values.get("nextheader").asNumeric().intValue(), is(equalTo(Protocol.TCP)));
        assertThat(values.get("hoplimit").asNumeric().intValue(), is(equalTo(64)));
        assertThat(values.get("sourceaddress").getValue(), is(equalTo(hexStringToBytes("20010470e5bfdead49572174e82c4887"))));
        assertThat(values.get("destinationaddress").getValue(), is(equalTo(hexStringToBytes("2607f8b0400c0c03000000000000001a"))));
        assertThat(values.get("sourceport").asNumeric().intValue(), is(equalTo(63943)));
        assertThat(values.get("destinationport").asNumeric().intValue(), is(equalTo(25)));
        assertThat(values.get("doresfl").getValue(), is(equalTo(hexStringToBytes("8002"))));
        assertThat(values.get("windowsize").asNumeric().intValue(), is(equalTo(8192)));
        assertThat(values.get("tcpchecksum").getValue(), is(equalTo(hexStringToBytes("DA47"))));
    }

    @Test
    public void testLLMNR() throws IOException {
        final String hexString = "6000000000241101fe800000000000006d49f772ed9bd16aff020000000000000000000000010003ed6714eb00244230bba7000000010000000000000a73616d73756e672d70630000010001";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv6.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("vertraflo").getValue(), is(equalTo(hexStringToBytes("60000000"))));
        assertThat(new String(values.get("value").getValue(), StandardCharsets.UTF_8), is(equalTo("samsung-pc")));
    }

    @Test
    public void testIPv4InIPv6() throws IOException {
        final String hexString = "60000000008b04f62402f00000018e0100000000000055552607fcd00100230000000000b1082a6b4500008b8caf0000402f75fe100000c8c034a69a3081880b0067178000068fb100083a76ff03002145000063000040003c115667ac102c03080808089f400035004f2d23a62c01000001000000000000357871742d6465746563742d6d6f6465322d39373731326538382d313637612d343562392d393365652d39313331343065373636373800001c0001";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv6.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("vertraflo").getValue(), is(equalTo(hexStringToBytes("60000000"))));
        assertThat(values.get("protocol").asNumeric().intValue(), is(equalTo(Protocol.GRE)));
    }

    @Test
    public void testUDPDNS() throws IOException {
        final String hexString = "60000000005d1140200106100b10007d02065bfffef11183200106100b10007db942d98a864e87430035bbfd005d9bd9000185000001000000010001026964067365727665720000100003c00c00060003000151800023c00c0a686f73746d6173746572c00c000000000000708000001c2000093a80000151800000291000000080000000";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv6.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("nextheader").asNumeric().intValue(), is(equalTo(Protocol.UDP)));
        assertThat(values.get("hoplimit").asNumeric().intValue(), is(equalTo(64)));
        assertThat(ipStringFromBytes(values.get("sourceaddress").getValue()), is(equalTo("2001:610:b10:7d:206:5bff:fef1:1183")));
        assertThat(ipStringFromBytes(values.get("destinationaddress").getValue()), is(equalTo("2001:610:b10:7d:b942:d98a:864e:8743")));
        assertThat(values.get("sourceport").asNumeric().intValue(), is(equalTo(53)));
        assertThat(values.get("udplength").asNumeric().intValue(), is(equalTo(93)));
        assertThat(values.get("qdcount").asNumeric().intValue(), is(equalTo(1)));
        assertThat(values.get("arcount").asNumeric().intValue(), is(equalTo(1)));
    }

    @Test
    public void testTCPDNS() throws IOException {
        final String hexString = "6000000000650640200106100b10007d02065bfffef11183200106100b10007db942d98a864e87430035baa8e8d5dba569ff27ba801800b391a900000101080aff6dc44f1874070100430006850000010001000100000776657273696f6e0462696e640000100003c00c0010000300000000000b0a392e342e312d50312e31c00c00020003000000000002c00c";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv6.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("nextheader").asNumeric().intValue(), is(equalTo(Protocol.TCP)));
        assertThat(values.get("hoplimit").asNumeric().intValue(), is(equalTo(64)));
        assertThat(ipStringFromBytes(values.get("sourceaddress").getValue()), is(equalTo("2001:610:b10:7d:206:5bff:fef1:1183")));
        assertThat(ipStringFromBytes(values.get("destinationaddress").getValue()), is(equalTo("2001:610:b10:7d:b942:d98a:864e:8743")));
        assertThat(values.get("sourceport").asNumeric().intValue(), is(equalTo(53)));
        assertThat(values.get("qdcount").asNumeric().intValue(), is(equalTo(1)));
        assertThat(values.get("value").asString(), is(equalTo("bind")));
    }

    @Test
    public void testPort53TCPNotDNS() throws IOException {
        final String hexString = "6000000000280640200106100b10007db942d98a864e8743200106100b10007d02065bfffef11183b6c500351b744ef600000000a002708072440000020405a00402080a1874010a0000000001030307";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv6.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("nextheader").asNumeric().intValue(), is(equalTo(Protocol.TCP)));
        assertThat(values.get("destinationport").asNumeric().intValue(), is(equalTo(53)));
    }

    @Test
    public void testInvalidIPVersion() throws IOException {
        final String hexString = "9000000000280640200106100b10007db942d98a864e8743200106100b10007d02065bfffef11183b6c500351b744ef600000000a002708072440000020405a00402080a1874010a0000000001030307";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv6.FORMAT);
        assertFalse(result.succeeded());
    }

    @Test
    public void testInvalidIPVersion2() throws IOException {
        final String hexString = "0000000000280640200106100b10007db942d98a864e8743200106100b10007d02065bfffef11183b6c500351b744ef600000000a002708072440000020405a00402080a1874010a0000000001030307";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv6.FORMAT);
        assertFalse(result.succeeded());
    }

    @Test
    public void testZeroPayloadLength() throws IOException {
        final String hexString = "6000000000000640200106100b10007db942d98a864e8743200106100b10007d02065bfffef11183b6c500351b744ef600000000a002708072440000020405a00402080a1874010a0000000001030307";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv6.FORMAT);
        assertFalse(result.succeeded());
    }

    @Test
    public void testMaxPayloadLength() throws IOException {
        final String hexString = "6000000000FF0640200106100b10007db942d98a864e8743200106100b10007d02065bfffef11183b6c500351b744ef600000000a002708072440000020405a00402080a1874010a0000000001030307";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv6.FORMAT);
        assertFalse(result.succeeded());
    }

    @Test
    public void testPayloadShortherThanGiven() throws IOException {
        final String hexString = "6000000000280640200106100b10007db942d98a864e8743200106100b10007d02065bfffef11183b6c500351b744ef600000000a002708072440000020405a00402080a1874010a";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv6.FORMAT);
        assertFalse(result.succeeded());
    }

    @Test
    public void testPayloadShortherThanGiven2() throws IOException {
        final String hexString = "60000000005d1140200106100b10007d02065bfffef11183200106100b10007db942d98a864e87430035bbfd005d9bd9000185000001000000010001026964067365727665720000100003c00c00060003000151800023c00c0a686f73746d6173746572c00c000000000000708000001c2000093a800001518000002910000000800000";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv6.FORMAT);
        assertFalse(result.succeeded());
    }

    @Test
    public void testCutOffStream() throws IOException {
        final String hexString = "6000000000FF0640200106100b10007db942d98a864e8743200106100b10007d02065bfffef11183b6";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv6.FORMAT);
        assertFalse(result.succeeded());
    }
}