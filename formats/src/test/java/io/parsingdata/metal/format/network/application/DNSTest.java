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
package io.parsingdata.metal.format.network.application;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.format.network.Util.hexStringToBytes;
import static io.parsingdata.metal.format.network.Util.ipStringFromBytes;
import static io.parsingdata.metal.format.network.Util.ipv4StringFromInt;
import static io.parsingdata.metal.util.Util.parse;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.ParseValueList;

public class DNSTest {

    @Test
    public void testQuestion() throws IOException {
        final String hexString = "908E010000010000000000000361637306787334616C6C036E65740000010001";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, DNS.FORMAT);
        final ParseGraph values = result.getEnvironment().order.reverse();

        assertTrue(result.succeeded());
        assertThat(values.get("messageid").getValue(), is(equalTo(new byte[]{(byte) 0x90, (byte) 0x8E})));
        assertThat(values.get("qdcount").asNumeric().intValue(), is(equalTo(1)));
        assertThat(values.get("ancount").asNumeric().intValue(), is(equalTo(0)));

        final ParseValueList lengthList = values.getAll("length");
        final ParseValueList valueList = values.getAll("value");

        // test for acs.xs4all.net
        ParseValue length = getAtIndex(lengthList, 0);
        ParseValue value = getAtIndex(valueList, 0);

        assertThat(length.asNumeric().intValue(), is(equalTo(3)));
        assertThat(value.asString(), is(equalTo("acs")));

        length = getAtIndex(lengthList, 1);
        value = getAtIndex(valueList, 1);

        assertThat(length.asNumeric().intValue(), is(equalTo(6)));
        assertThat(value.asString(), is(equalTo("xs4all")));

        length = getAtIndex(lengthList, 2);
        value = getAtIndex(valueList, 2);

        assertThat(length.asNumeric().intValue(), is(equalTo(3)));
        assertThat(value.asString(), is(equalTo("net")));
    }

    @Test
    public void testMultiIPAnswer() throws IOException {
        final String hexString = "000381800001000b0000000006676f6f676c6503636f6d0000010001c00c000100010000000400044a7dec23c00c000100010000000400044a7dec25c00c000100010000000400044a7dec27c00c000100010000000400044a7dec20c00c000100010000000400044a7dec28c00c000100010000000400044a7dec21c00c000100010000000400044a7dec29c00c000100010000000400044a7dec22c00c000100010000000400044a7dec24c00c000100010000000400044a7dec2ec00c000100010000000400044a7dec26";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, DNS.FORMAT);
        final ParseGraph values = result.getEnvironment().order.reverse();

        assertTrue(result.succeeded());
        assertThat(values.get("messageid").getValue(), is(equalTo(new byte[]{0x00, 0x03})));
        assertThat(values.get("qdcount").asNumeric().intValue(), is(equalTo(1)));
        assertThat(values.get("ancount").asNumeric().intValue(), is(equalTo(11)));

        final ParseValueList anrlengthList = values.getAll("anrlength");
        final ParseValueList anrdataList = values.getAll("anrdata");

        assertThat(anrlengthList.size, is(equalTo(11L)));

        // just hand picking a couple of IP's to test, not all 11
        ParseValue value = getAtIndex(anrdataList, 0);
        assertThat(ipv4StringFromInt(value.asNumeric().intValue()), is(equalTo("74.125.236.35")));
        value = getAtIndex(anrdataList, 3);
        assertThat(ipv4StringFromInt(value.asNumeric().intValue()), is(equalTo("74.125.236.32")));
        value = getAtIndex(anrdataList, 6);
        assertThat(ipv4StringFromInt(value.asNumeric().intValue()), is(equalTo("74.125.236.41")));
        value = getAtIndex(anrdataList, 9);
        assertThat(ipv4StringFromInt(value.asNumeric().intValue()), is(equalTo("74.125.236.46")));
    }

    @Ignore("Currently does't work because anrdata doesn't get parsed as a label format")
    @Test
    public void testMultiHostAnswer() throws IOException {
        final String hexString = "b6de8180000100030000000003777777076d6f7a696c6c61036f726700001c0001c00c0005000100000021001b066d6f7a6f72670664796e656374076d6f7a696c6c61036e657400c02d000500010000000300170c626564726f636b2d70726f64037a6c6203706878c03bc054001c000100000001001026200101800800050000000000020001";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, DNS.FORMAT);
        final ParseGraph values = result.getEnvironment().order.reverse();

        assertTrue(result.succeeded());
        assertThat(values.get("qdcount").asNumeric().intValue(), is(equalTo(1)));
        assertThat(values.get("ancount").asNumeric().intValue(), is(equalTo(3)));

        final ParseValueList valueList = values.getAll("value");

        // test for www.mozilla.org
        ParseValue value = getAtIndex(valueList, 0);
        assertThat(value.asString(), is(equalTo("www")));
        value = getAtIndex(valueList, 1);
        assertThat(value.asString(), is(equalTo("mozilla")));
        value = getAtIndex(valueList, 2);
        assertThat(value.asString(), is(equalTo("org")));

        // test for mozorg.dynect.mozilla.net
        value = getAtIndex(valueList, 6);
        assertThat(value.asString(), is(equalTo("mozorg")));
        value = getAtIndex(valueList, 7);
        assertThat(value.asString(), is(equalTo("dynect")));
        value = getAtIndex(valueList, 8);
        assertThat(value.asString(), is(equalTo("mozilla")));
        value = getAtIndex(valueList, 9);
        assertThat(value.asString(), is(equalTo("net")));

        // bedrock-prod.zlb.phx.mozilla.net
        value = getAtIndex(valueList, 10);
        assertThat(value.asString(), is(equalTo("bedrock-prod")));
        value = getAtIndex(valueList, 11);
        assertThat(value.asString(), is(equalTo("zlb")));
        value = getAtIndex(valueList, 12);
        assertThat(value.asString(), is(equalTo("phx")));
        value = getAtIndex(valueList, 13);
        assertThat(value.asString(), is(equalTo("mozilla")));
        value = getAtIndex(valueList, 14);
        assertThat(value.asString(), is(equalTo("net")));

        // AAAA address of bedrock-prod.zlb.phx.mozilla.net
        final ParseValueList anrDataList = values.getAll("anrdata");
        assertThat(ipStringFromBytes(getAtIndex(anrDataList.reverse(), 0).getValue()), is(equalTo("2620:101:8008:5:0:0:2:1")));
    }

    @Test // AXFR = Asynchronous Full Transfer Zone
    public void testAXFR() throws IOException {
        final String hexString = "000080800001000400000000046574617303636f6d0000fc0001c00c0006000100000e10002f0d747261696e696e673230303370000a686f73746d617374657200000000030000003c000002580001518000000e10c00c0002000100000e10000f0d747261696e696e673230303370000777656c636f6d65c00c0001000100000e10000401010101c00c0006000100000e10002f0d747261696e696e673230303370000a686f73746d617374657200000000030000003c000002580001518000000e10";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, DNS.FORMAT);
        final ParseGraph values = result.getEnvironment().order.reverse();

        assertTrue(result.succeeded());
        assertThat(values.get("qdcount").asNumeric().intValue(), is(equalTo(1)));
        assertThat(values.get("ancount").asNumeric().intValue(), is(equalTo(4)));

        final ParseValueList anrDataList = values.getAll("anrdata");
        final ParseValueList valueList = values.getAll("value");

        assertThat(anrDataList.size, is(equalTo(4L)));
        assertThat(valueList.size, is(equalTo(3L)));

        ParseValue value = getAtIndex(anrDataList, 1);
        assertThat(value.asString().trim(), is(equalTo("training2003p")));
        value = getAtIndex(anrDataList, 2);
        assertThat(ipv4StringFromInt(value.asNumeric().intValue()), is(equalTo("1.1.1.1")));
    }

    @Test // IXFR = Incremental Zone Transfer
    public void testIXFR() throws IOException {
        final String hexString = "400080800001000500000000046574617303636f6d0000fb0001c00c0006000100000e10002f0d747261696e696e673230303370000a686f73746d617374657200000000040000003c000002580001518000000e10c00c0006000100000e100018c026c035000000030000003c000002580001518000000e10c00c0006000100000e100018c026c035000000040000003c000002580001518000000e1005696e646578c00c0001000100000e10000401010164c00c0006000100000e100018c026c035000000040000003c000002580001518000000e10";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, DNS.FORMAT);
        final ParseGraph values = result.getEnvironment().order.reverse();

        assertTrue(result.succeeded());
        assertThat(values.get("qdcount").asNumeric().intValue(), is(equalTo(1)));
        assertThat(values.get("ancount").asNumeric().intValue(), is(equalTo(5)));

        final ParseValueList anrDataList = values.getAll("anrdata");
        final ParseValueList valueList = values.getAll("value");

        assertThat(anrDataList.size, is(equalTo(5L)));
        assertThat(valueList.size, is(equalTo(3L)));

        final ParseValue value = getAtIndex(anrDataList, 3);
        assertThat(ipv4StringFromInt(value.asNumeric().intValue()), is(equalTo("1.1.1.100")));
    }

    @Test
    public void testZoneTransfer() throws IOException {
        final String hexString = "000080800001000400000000046574617303636f6d0000fc0001c00c0006000100000e10002f0d747261696e696e673230303370000a686f73746d617374657200000000030000003c000002580001518000000e10c00c0002000100000e10000f0d747261696e696e673230303370000777656c636f6d65c00c0001000100000e10000401010101c00c0006000100000e10002f0d747261696e696e673230303370000a686f73746d617374657200000000030000003c000002580001518000000e10";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, DNS.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("messageid").getValue(), is(equalTo(new byte[]{0x00, 0x00})));
        assertThat(values.get("qdcount").asNumeric().intValue(), is(equalTo(1)));
        assertThat(values.get("ancount").asNumeric().intValue(), is(equalTo(4)));
    }

    @Test // SOA = Start of Authority
    public void testSOA() throws IOException {
        final String hexString = "0005818300010000000100000973757065727573657203636f6d06686f6c6d6573026e6c00001c0001c01a0006000100000db00027036e7333c01a0a686f73746d6173746572c01a781c01d900001c200000021c00093a8000015180";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, DNS.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("messageid").getValue(), is(equalTo(new byte[]{0x00, 0x05})));
        assertThat(values.get("qdcount").asNumeric().intValue(), is(equalTo(1)));
        assertThat(values.get("ancount").asNumeric().intValue(), is(equalTo(0)));
        assertThat(values.get("nscount").asNumeric().intValue(), is(equalTo(1)));
        assertThat(values.get("nsrdata").getValue(), is(equalTo(hexStringToBytes("036E7333C01A0A686F73746D6173746572C01A781C01D900001C200000021C00093A8000015180"))));
    }

    @Test
    public void testDNSKey() throws IOException {
        final String hexString = "20a3010000010000000000010469657466036f726700003000010000291000000080000000";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, DNS.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("messageid").getValue(), is(equalTo(new byte[]{0x20, (byte) 0xA3})));
        assertThat(values.get("qdcount").asNumeric().intValue(), is(equalTo(1)));
        assertThat(values.get("ancount").asNumeric().intValue(), is(equalTo(0)));
        assertThat(values.get("arcount").asNumeric().intValue(), is(equalTo(1)));
    }

    private ParseValue getAtIndex(final ParseValueList list, final int index) {
        if (index == 0) {
            return list.head;
        }
        return getAtIndex(list.tail, index - 1);
    }
}
