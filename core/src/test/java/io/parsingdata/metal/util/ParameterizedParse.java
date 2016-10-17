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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;

@Ignore
@RunWith(Parameterized.class)
public class ParameterizedParse {

    private final Token token;
    private final Environment environment;
    private final Encoding encoding;
    private final boolean result;

    public ParameterizedParse(final Token token, final Environment environment, final Encoding encoding, final boolean result) {
        this.token = token;
        this.environment = environment;
        this.encoding = encoding;
        this.result = result;
    }

    @Test
    public void test() throws IOException {
        Assert.assertEquals(result, token.parse(environment, encoding).succeeded);
    }

}
