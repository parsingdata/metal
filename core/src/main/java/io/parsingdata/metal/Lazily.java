/*
 * Copyright 2017 SWAT.engineering BV
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

import java.util.function.Supplier;

public final class Lazily {

    private Lazily() {}

    /**
     * Constructs a lazily calculated value (of type T) the first time it is retrieved.  <br/><br/>
     *
     * Does not give any guarantees on how often the calculator is called in case of multi-threading.
     * @param calculator the closure that calculates the actual value.
     * @return a LazilyCalculated value store
     */
    public static <T> LazilyCalculated<T> calculate(Supplier<T> calculator) {
        return new LazilyCalculated<T>() {
            private T value = null;

            @Override
            public T get() {
                T result = value;
                if (result == null) {
                    result = calculator.get();
                    value = result;
                }
                return result;
            }
        };
    }

    /**
     * Constructs a lazily calculated value (of type T) the first time it is retrieved.  <br/><br/>
     *
     * This lazily calculated value will only trigger the calculator once at the costs of more locking an synchronisation.
     * @param calculator the closure that calculates the actual value (take care not to call the get on yourself).
     * @return a LazilyCalculated value store
     */
    public static <T> LazilyCalculated<T> calculateOnlyOnce(Supplier<T> calculator) {
        return new LazilyCalculated<T>() {
            private volatile T value = null; // volatile to get the correct memory/jvm caching behavior

            @Override
            public T get() {
                T result = value;
                if (result == null) {
                    synchronized (this) {
                        result = value;
                        if (result == null) {
                            // it is still null, so we are the ones to calculate the actual value
                            result = calculator.get();
                            value = result;
                        }
                    }
                }
                return result;
            }
        };
    }
}
