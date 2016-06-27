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

import io.parsingdata.metal.data.ParseValue;

/**
 * Parsed value processor interface.
 *
 * Processes individual {@link ParseValue}s. Examples of processors are:
 *
 * - Processors that serialize values back to an output stream
 * - Processors that analyze a parsed value, e.g. to analyze a token's coverage
 *
 * @author Netherlands Forensic Institute.
 */
public interface ParseValueProcessor {

    /**
     * Process a {@link ParseValue}.
     *
     * @param value the ParseValue to process
     */
    void process(ParseValue value);
}