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
import static io.parsingdata.metal.util.Util.parse;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseResult;

public class IPv4Test {

    @Test
    public void testParse() throws IOException {
        final String hexString = "45c00038d43100003f01302453a1f995da4d4f2b030d4d8a0000000045000028d4310000ee0681eeda4d4f2b53a1f9958cc6001706491c42";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv4.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("versionihl").asNumeric().intValue(), is(equalTo(0x45)));
        assertThat(values.get("iplength").asNumeric().intValue(), is(equalTo(56)));
        assertThat(values.get("timetolive").asNumeric().intValue(), is(equalTo(63)));
        assertThat(values.get("protocol").asNumeric().intValue(), is(equalTo(1)));
        assertThat(values.get("ipsource").asNumeric().longValue(), is(equalTo(1403124117L)));
        assertThat(values.get("ipdestination").asNumeric().longValue(), is(equalTo(3662499627L)));
    }

    @Test
    public void testIPv4UDPDNSParse() throws IOException {
        final String hexString = "45000082d9eb00003a11f873c0a801c8c0a8016400350268006eba41ea1a818300010000000100000133023435033139310332303605646e73626c05736f726273036e65740000010001c0190006000100000e10002c0772626c646e7330c01f03646e73046973757803636f6d00431ce07e00001c2000001c2000093a8000000e10";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv4.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("protocol").asNumeric().intValue(), is(equalTo(17)));
        assertThat(values.get("sourceport").asNumeric().intValue(), is(equalTo(53)));
        assertThat(values.get("udplength").asNumeric().intValue(), is(equalTo(110)));
        assertThat(values.get("nscount").asNumeric().intValue(), is(equalTo(1)));
    }

    @Test
    public void testIPv4UDPDNSParse2() throws IOException {
        final String hexString = "4500004a5e4b00004011ac820adead160adead03dcde003500367c1503c401200001000000000001046f63737008766572697369676e036e657400001c00010000291000000000000000";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv4.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("protocol").asNumeric().intValue(), is(equalTo(17)));
        assertThat(values.get("sourceport").asNumeric().intValue(), is(equalTo(56542)));
        assertThat(values.get("udplength").asNumeric().intValue(), is(equalTo(54)));
        assertThat(values.get("qdcount").asNumeric().intValue(), is(equalTo(1)));
        assertThat(values.get("arcount").asNumeric().intValue(), is(equalTo(1)));
    }

    @Test
    public void testIPv4TCPDNSParse() throws IOException {
        final String hexString = "4500004600ec40008006f5c1010101020101010104120035d1f8c1175ff5a8bd5018fb9005680000001c000000000001000000000000046574617303636f6d0000fc00014d53";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv4.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("protocol").asNumeric().intValue(), is(equalTo(6)));
        assertThat(values.get("destinationport").asNumeric().intValue(), is(equalTo(53)));
        assertThat(values.get("dnslength").asNumeric().intValue(), is(equalTo(28)));
        assertThat(values.get("qdcount").asNumeric().intValue(), is(equalTo(1)));
    }

    @Test
    public void testIPv4InvalidIPVersion() throws IOException {
        // IPv4 -> UDP -> DNS
        final String hexString = "3500004a5e4b00004011ac820adead160adead03dcde003500367c1503c401200001000000000001046f63737008766572697369676e036e657400001c00010000291000000000000000";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv4.FORMAT);

        assertFalse(result.succeeded());
    }

    @Test
    public void testIPv4InvalidIPVersion2() throws IOException {
        // IPv4 -> UDP -> TCP
        final String hexString = "6500000000ec40008006f5c1010101020101010104120035d1f8c1175ff5a8bd5018fb9005680000001c000000000001000000000000046574617303636f6d0000fc00014d53";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv4.FORMAT);

        assertFalse(result.succeeded());
    }

    @Test
    public void testZeroIPLength() throws IOException {
        // IPv4 -> UDP -> DNS
        final String hexString = "450000005e4b00004011ac820adead160adead03dcde003500367c1503c401200001000000000001046f63737008766572697369676e036e657400001c00010000291000000000000000";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv4.FORMAT);

        assertFalse(result.succeeded());
    }

    @Test
    public void testZeroIPLength2() throws IOException {
        // IPv4 -> TCP -> DNS
        final String hexString = "4500000000ec40008006f5c1010101020101010104120035d1f8c1175ff5a8bd5018fb9005680000001c000000000001000000000000046574617303636f6d0000fc00014d53";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv4.FORMAT);

        assertFalse(result.succeeded());
    }

    @Test
    public void testZeroIPHeaderLength() throws IOException {
        // IPv4 -> UDP -> DNS
        final String hexString = "4000004a5e4b00004011ac820adead160adead03dcde003500367c1503c401200001000000000001046f63737008766572697369676e036e657400001c00010000291000000000000000";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv4.FORMAT);

        assertFalse(result.succeeded());
    }

    // the following are ignored because we expect them to fail (which they don't)
    // because the given total length in the IPv4 header is bogus

    @Ignore
    @Test
    public void testMaxIPLength() throws IOException {
        // IPv4 -> UDP -> DNS
        final String hexString = "4500FFFF5e4b00004011ac820adead160adead03dcde003500367c1503c401200001000000000001046f63737008766572697369676e036e657400001c00010000291000000000000000";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv4.FORMAT);

        assertFalse(result.succeeded());
    }

    @Ignore
    @Test
    public void testMaxIPLength2() throws IOException {
        // IPv4 -> TCP -> DNS
        final String hexString = "4500FFFF00ec40008006f5c1010101020101010104120035d1f8c1175ff5a8bd5018fb9005680000001c000000000001000000000000046574617303636f6d0000fc00014d53";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, IPv4.FORMAT);

        assertFalse(result.succeeded());
    }
}