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

package io.parsingdata.metal;

/**
 * A version of the {@link Trampoline} interface without a checked
 * {@link java.io.IOException} on the {@link #next()} and
 * {@link #computeResult()} methods.
 *
 * @see Trampoline
 */
public interface SafeTrampoline<T> extends Trampoline<T> {

    @Override
    SafeTrampoline<T> next();

    @Override
    default T computeResult() {
        SafeTrampoline<T> current = this;
        while (current.hasNext()) {
            current = current.next();
        }
        return current.result();
    }

    static <T> SafeTrampoline<T> complete(final CompletedTrampoline<T> completedTrampoline) { return completedTrampoline; }

    interface CompletedTrampoline<T> extends Trampoline.CompletedTrampoline<T>, SafeTrampoline<T> {

        default SafeTrampoline<T> next() { throw new UnsupportedOperationException("A CompletedTrampoline does not have a next computation."); }

    }

    static <T> SafeTrampoline<T> intermediate(final IntermediateTrampoline<T> intermediateTrampoline) { return intermediateTrampoline; }

    interface IntermediateTrampoline<T> extends Trampoline.IntermediateTrampoline<T>, SafeTrampoline<T> {}

}
