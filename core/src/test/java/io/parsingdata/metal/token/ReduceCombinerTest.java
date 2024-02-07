/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

package io.parsingdata.metal.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.util.ParseStateFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.encoding.Encoding;

/**
 * The ReduceCombinerTest class contains test methods for classes that uses the {@link Stream#reduce} method.
 * This method requires a combiner, since streams may run in parallel.
 * To test these combiners we need to force some streams to run parallel, because we never run them in parallel in production.
 */
class ReduceCombinerTest {

    @Test
    public void seqTest() throws IllegalAccessException, NoSuchFieldException {
        final Seq seq = new Seq("test", Encoding.DEFAULT_ENCODING, any("a"), any("b"), any("c"));

        setFinalFieldAndAssert(seq, "tokens", new ParallelImmutableList<>(seq.tokens), () -> {
            final Environment environment = new Environment(stream(1, 2, 3), Encoding.DEFAULT_ENCODING);
            final Exception e = assertThrows(UnsupportedOperationException.class, () -> seq.parseImpl(environment));
            final String actual = e.getMessage();
            final String expected = "Parallel processing of streams is not implemented.";
            assertTrue(actual.endsWith(expected), "Unexpected ending of message. Got:\n" + actual + "\nbut expected to end with:\n" + expected);
        });
    }

    private static void setFinalFieldAndAssert(final Seq seq, final String fieldName, final Object fieldValue, final Runnable runnable) throws NoSuchFieldException, IllegalAccessException {
        final Field tokensField = seq.getClass().getDeclaredField(fieldName);
        boolean isAccessible = tokensField.canAccess(seq);
        try {
            ReflectionUtils.makeAccessible(tokensField);
            tokensField.set(seq, fieldValue);

            runnable.run();
        }
        finally {
            tokensField.setAccessible(isAccessible);
        }
    }

    public static class ParallelImmutableList<T> extends ImmutableList<T> {
        public ParallelImmutableList(final ImmutableList<T> tokens) {
            super(tokens);
        }

        @Override
        public Stream<T> stream() {
            return super.stream().parallel();
        }
    }
}