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
package io.parsingdata.metal.util.serialize.transform;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.GraphUtil;

/**
 * Transforms parsed values.
 * <p>
 * Whenever a parse value is transformed, the {@link #transform(ParseValue, Environment)} method
 * will receive the value to transform, and an {@link Environment} that should be based on
 * the {@link #context()} Token.
 * <p>
 * An example: checksum recalculation in a IPv4 header.
 * IPv4 headers contain a checksum of that header. If we change a value in the header,
 * we need to recalculate this checksum (for example, when we change an IP address).
 * If the {@link #context()} for such a transformer is the IPv4 header token, the
 * {@link Environment} that is passed into {@link #transform(ParseValue, Environment)}
 * should only contain the IPv4 header values.
 *
 * @author Netherlands Forensic Institute.
 */
public interface ParseValueTransformer {

    /**
     * The tokens that should limit the scope of the environment that is passed into {@link #transform(ParseValue, Environment)}.
     * All tokens should be present in the path from root to the value that is being transformed, in order.
     * The environment passed to this transformer is limited by the final token in this array.
     * <p>
     * Also see {@link GraphUtil#findSubGraph(Environment, ParseValue, Token...)}.
     *
     * @return a token representing a scope
     */
    Token[] context();

    /**
     * Transform a parsed value.
     *
     * @param value the value to transform
     * @param environment the environment the transformer can use for its transformation of the value
     * @return the transformed value
     */
    ParseValue transform(ParseValue value, final Environment environment);
}