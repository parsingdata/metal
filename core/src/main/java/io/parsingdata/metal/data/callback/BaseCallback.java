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

package io.parsingdata.metal.data.callback;

import java.util.function.BiConsumer;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;

public abstract class BaseCallback implements BiConsumer<Token, ParseResult> {

    @Override
    public void accept(final Token token, final ParseResult result) {
        if (result.succeeded) {
            handleSuccess(token, result.environment);
        } else {
            handleFailure(token, result.environment);
        }
    }

    protected abstract void handleSuccess(Token token, Environment environment);
    protected abstract void handleFailure(Token token, Environment environment);

}
