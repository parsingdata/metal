package io.parsingdata.metal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.tie;
import static io.parsingdata.metal.Shorthand.token;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.util.EnvironmentFactory.env;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.data.Selection;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;

class ImmutableObjectTest {

    @Test
    void checkHashCode() {
        final ImmutableObject immutableObject = new ImmutableObject() {
            @Override
            public String toString() {return "test";}
            @Override
            public boolean equals(Object obj) {return false;}
            @Override
            public int immutableHashCode() {return 25;}
        };
        assertEquals(25, immutableObject.hashCode());
    }

    @Test
    void calculateHashOnlyOnce() {
        final AtomicInteger counter = new AtomicInteger(0);
        final ImmutableObject immutableObject = new ImmutableObject() {
            @Override
            public String toString() {return "";}
            @Override
            public boolean equals(Object obj) {return false;}
            @Override
            public int immutableHashCode() {
                counter.incrementAndGet();
                return 15;
            }
        };
        assertEquals(15, immutableObject.hashCode());
        assertEquals(15, immutableObject.hashCode());
        assertEquals(15, immutableObject.hashCode());
        assertEquals(1, counter.get());
    }

    @Test
    void performanceTest() {
        // This test would take way too much time without hash caching.
        final int dataBlockCount = 32;
        final int dataSize = 64;
        final byte[] input = new byte[dataBlockCount*dataSize];
        // This token contains recursive tokens to create large ParseGraphs.
        final Token deep = repn(
            seq(
                def("data", dataSize),
                tie(
                    seq("token",
                        def("byte", 1),
                        opt(token("token"))
                    ),
                    last(ref("data"))
                )
            ),
            con(dataBlockCount)
        );
        final Optional<ParseState> result = deep.parse(env(createFromByteStream(new InMemoryByteStream(input))));
        assertTrue(result.isPresent());

        ImmutableList<ParseValue> allValues = Selection.getAllValues(result.get().order, x -> true);
        assertThat(allValues.size, equalTo(2080L));

        final Map<ParseValue, Value> values = new HashMap<>();
        while (allValues != null && allValues.head != null) {
            values.put(allValues.head, allValues.head);
            allValues = allValues.tail;
        }
        assertThat(values.size(), equalTo(2080));
    }

}