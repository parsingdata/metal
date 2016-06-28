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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.format.network.TestUtil.hexStringToBytes;
import static io.parsingdata.metal.util.Util.parse;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseResult;

public class UDPTest {

    @Test
    public void testParse() throws IOException {
        final String hexString = "87B0003500283FEA908E010000010000000000000361637306787334616C6C036E65740000010001";
        final byte[] data = hexStringToBytes(hexString);
        final ParseResult result = parse(data, UDP.FORMAT);
        final ParseGraph values = result.getEnvironment().order;

        assertTrue(result.succeeded());
        assertThat(values.get("sourceport").asNumeric().intValue(), is(equalTo(34736)));
        assertThat(values.get("destinationport").asNumeric().intValue(), is(equalTo(53)));
        assertThat(values.get("udplength").asNumeric().intValue(), is(equalTo(40)));
        assertThat(values.get("udpchecksum").asNumeric().intValue(), is(equalTo(16362)));
    }
}