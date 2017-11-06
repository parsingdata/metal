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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static io.parsingdata.metal.util.TokenDefinitions.eq;
import static io.parsingdata.metal.util.TokenDefinitions.eqRef;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.parsingdata.metal.token.Token;

@RunWith(JUnit4.class)
public class BackTrackNameBindTest {

    private final Token _choiceRef = seq(any("a"),
                                         cho(seq(any("a"), eqRef("b", "a")),
                                             seq(def("b", con(1), not(Shorthand.eq(ref("a")))), any("c"))));

    private final Token _repeatRef = seq(rep(eq("a", 42)),
                                         rep(def("b", con(1), not(Shorthand.eq(last(ref("a")))))));

    @Test
    public void choiceRefLeft() throws IOException {
        assertTrue(_choiceRef.parse(env(stream(1, 2, 2))).isPresent());
    }

    @Test
    public void choiceRefRight() throws IOException {
        assertTrue(_choiceRef.parse(env(stream(1, 2, 3))).isPresent());
    }

    @Test
    public void choiceRefNone() throws IOException {
        assertFalse(_choiceRef.parse(env(stream(1, 1, 2))).isPresent());
    }

    @Test
    public void repeatRef() throws IOException {
        assertTrue(_repeatRef.parse(env(stream(42, 42, 42, 21, 21, 21))).isPresent());
    }

}
