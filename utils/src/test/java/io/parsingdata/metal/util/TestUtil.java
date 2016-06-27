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
