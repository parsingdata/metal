package io.parsingdata.metal;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;
import org.junit.Test;

import java.io.IOException;

import static io.parsingdata.metal.Shorthand.*;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SubStructTableTest {

    private final Token struct =
        seq(def("header", con(1), eq(con(42))),
            def("footer", con(1), eq(con(84))));

    private final Token table =
        seq(def("tableSize", con(1)),
            repn(def("pointer", con(1)), ref("tableSize")),
            sub(struct, ref("pointer")));

    @Test
    public void table() throws IOException {
        final Environment env = stream(3, 6, 4, 9, 42, 84, 42, 84, 0, 42, 84);
                            /* offset: 0, 1, 2, 3,  4,  5,  6,  7, 8,  9, 10
                             * count:  ^
                             * pointers:  ^, ^, ^
                             * ref1:      +----------------^^--^^
                             * ref2:         +-----^^--^^
                             * ref3:            +---------------------^^--^^
                             */
        final ParseResult res = table.parse(env, enc());
        assertTrue(res.succeeded());
        assertEquals(4, res.getEnvironment().offset);
        final ParseGraph order = res.getEnvironment().order;
        checkStruct(order.head.asGraph().head.asGraph());
        checkStruct(order.head.asGraph().tail.head.asGraph());
        checkStruct(order.head.asGraph().tail.tail.head.asGraph());
    }

    private void checkStruct(final ParseGraph graph) {
        assertEquals(84, graph.head.asGraph().head.asValue().asNumeric().intValue());
        assertEquals(42, graph.head.asGraph().tail.head.asValue().asNumeric().intValue());
    }

}
