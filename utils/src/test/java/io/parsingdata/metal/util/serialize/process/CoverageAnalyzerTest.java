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
package io.parsingdata.metal.util.serialize.process;

import static org.junit.Assert.assertEquals;

import static io.parsingdata.metal.util.TestUtil.range;
import static io.parsingdata.metal.util.TestUtil.ranges;

import java.util.List;

import org.apache.commons.lang3.Range;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.encoding.Encoding;

@RunWith(MockitoJUnitRunner.class)
public class CoverageAnalyzerTest {

    private static final Encoding ENC = new Encoding();
    private CoverageAnalyzer _parseValueProcessor;

    @Before
    public void setUp() {
        _parseValueProcessor = new CoverageAnalyzer();
    }

    @Test
    public void testSingleRange() {
        final ParseValue mock = val(0, 1);
        final List<Range<Long>> expected = ranges(range(0, 1));

        _parseValueProcessor.process(mock);

        assertEquals(expected, _parseValueProcessor.analysis().ranges());
    }

    @Test
    public void testMultiRange() {
        final ParseValue[] values = new ParseValue[]{val(0, 1), val(1, 1), val(10, 5), val(4, 1), val(8, 2)};
        final List<Range<Long>> expected = ranges(range(0, 2), range(4, 5), range(8, 15));

        for (final ParseValue value : values) {
            _parseValueProcessor.process(value);
        }

        assertEquals(expected, _parseValueProcessor.analysis().ranges());
    }

    @Test
    public void testBigRanges() {
        final ParseValue[] values = new ParseValue[]{val(0, 1000), val(50, 500), val(10000, 666), val(3000, 7500), val(123456789, 1)};
        final List<Range<Long>> expected = ranges(range(0, 1000), range(3000, 10666), range(123456789, 123456790));

        for (final ParseValue value : values) {
            _parseValueProcessor.process(value);
        }

        assertEquals(expected, _parseValueProcessor.analysis().ranges());
    }

    @Test
    public void testBatchOfIdentical() {
        final ParseValue[] values = new ParseValue[10000];
        for (int i = 0; i < values.length; i++) {
            values[i] = val(0, 1);
        }
        final List<Range<Long>> expected = ranges(range(0, 1));

        for (final ParseValue value : values) {
            _parseValueProcessor.process(value);
        }

        assertEquals(expected, _parseValueProcessor.analysis().ranges());
    }


    private ParseValue val(final long offset, final int bytesRead) {
        return new ParseValue("", "", ParseGraph.NONE, offset, new byte[bytesRead], ENC);
    }
}
