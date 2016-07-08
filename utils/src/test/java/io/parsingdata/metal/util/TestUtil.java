/*
 * Copyright 2013-2016 Netherlands Forensic Institute
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

package io.parsingdata.metal.util;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Range;

public final class TestUtil {

    private TestUtil() {
    }

    public static Range<Long> range(final long fromInclusive, final long toInclusive) {
        return Range.between(fromInclusive, toInclusive);
    }

    @SafeVarargs
    public static List<Range<Long>> ranges(final Range<Long>... ranges) {
        return Arrays.asList(ranges);
    }
}
