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

/**
 * A value that is only calculated the first time it is actually requested
 * @param <T>
 */
public interface LazilyCalculated<T> {
    /**
     * Get the value that is lazily calculated. <br /><br/>
     *
     * In some implementations this can cause a block in case there are multiple threads requesting the first get at the same time.
     * @return the value of type T that is the result of the calculation
     */
    T get();
}
