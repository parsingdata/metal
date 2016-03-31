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

import io.parsingdata.metal.token.Token;

public class Util {

    public static <T>T checkNotNull(final T argument, final String name) {
        if (argument == null) { throw new IllegalArgumentException("Argument " + name + " may not be null."); }
        return argument;
    }

    public static <T>T[] checkContainsNoNulls(final T[] argument, final String name) {
        checkNotNull(argument, name);
        for (final T arg : argument) {
            if (arg == null) { throw new IllegalArgumentException("Value in array " + name + " may not be null."); }
        }
        return argument;
    }

    public static String toString(final Token[] tokens) {
        if (tokens == null) { throw new RuntimeException("Argument tokens may not be null."); }
        if (tokens.length == 0) { return ""; }
        final StringBuilder out = new StringBuilder();
        for (int i = 0; i < tokens.length - 1; i++) {
            out.append(tokens[i].toString());
            out.append(", ");
        }
        return out.append(tokens[tokens.length - 1]).toString();
    }
}
