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

package io.parsingdata.metal.util.serialize.process;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Range;

import io.parsingdata.metal.data.ParseValue;

/**
 * Used to analyze and report about the 'coverage' of a parse result.
 * (i.e. which data was and was not parsed by a Metal token)
 *
 * @author Netherlands Forensic Institute.
 */
public final class CoverageAnalyzer implements ParseValueProcessor {

    private final CoverageAnalysis _analysis = new CoverageAnalysis();

    @Override
    public void process(final ParseValue value) {
        _analysis.setCovered(value.getOffset(), value.getValue().length);
    }

    public CoverageAnalysis analysis() {
        return _analysis;
    }

    /**
     * An analysis about the coverage serialization.
     *
     * @author Netherlands Forensic Institute.
     */
    public final class CoverageAnalysis {

        // A range represents from offset up to offset, so the first two bytes of an array have range [0, 2].
        private final List<Range<Long>> _ranges = new ArrayList<>();

        public void setCovered(final long offset, final long length) {
            _ranges.add(Range.between(offset, offset + length));
        }

        public List<Range<Long>> ranges() {
            if (!_ranges.isEmpty()) {
                return mergedRanges(_ranges);
            }
            return Collections.unmodifiableList(_ranges);
        }

        private List<Range<Long>> mergedRanges(final List<Range<Long>> ranges) {
            Collections.sort(ranges, new Comparator<Range<Long>>() {
                @Override
                public int compare(final Range<Long> x, final Range<Long> y) {
                    return Long.compare(x.getMinimum(), y.getMinimum());
                }
            });

            final List<Range<Long>> minimizedRanges = new ArrayList<>();
            final Iterator<Range<Long>> iterator = ranges.iterator();

            Range<Long> current = iterator.next();
            while (iterator.hasNext()) {
                final Range<Long> next = iterator.next();
                if (current.isOverlappedBy(next)) {
                    final long toInclusive = current.getMaximum() > next.getMaximum() ? current.getMaximum() : next.getMaximum();
                    current = Range.between(current.getMinimum(), toInclusive);
                }
                else {
                    minimizedRanges.add(current);
                    current = next;
                }
            }
            minimizedRanges.add(current);

            return minimizedRanges;
        }
    }
}