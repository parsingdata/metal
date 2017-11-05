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

import static org.junit.Assert.assertEquals;

import static io.parsingdata.metal.util.EnvironmentFactory.env;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;

@Ignore
@RunWith(Parameterized.class)
public class ParameterizedParse {

    @Parameter(0) public String description;
    @Parameter(1) public Token token;
    @Parameter(2) public ParseState parseState;
    @Parameter(3) public Encoding encoding;
    @Parameter(4) public boolean result;

    @Test
    public void test() throws IOException {
        assertEquals(result, token.parse(env(parseState, encoding)).isPresent());
    }

}
