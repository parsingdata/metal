package io.parsingdata.metal;

import static org.junit.Assert.assertEquals;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.str;
import static io.parsingdata.metal.util.EncodingFactory.le;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.EnvironmentFactory;

public class SequenceIdTest {

    @Test
    public void test() throws IOException {
        final Token token = seq(
            seq(
                def("id", 0),
                def("other", 0),
                cho(
                    def("zero", 0),
                    def("one", 1)
                )),
            str("str",
                def("last", 0)
            ));

        final Environment env = EnvironmentFactory.stream();
        final ParseResult result = token.parse(env, le());

        final StringBuilder builder = new StringBuilder();
        step(result.getEnvironment().order, builder);

        assertEquals(
            "[3] value: id\n" +
            "[4] value: other\n" +
            "[6] value: zero\n" +
            "[8] value: last\n", builder.toString());
    }

    private void step(final ParseItem item, final StringBuilder builder) {
        if (item == null) {
            return;
        }

        if (!item.isGraph()) {
            builder.append("[" + item.getSequenceId() + "] value: " + item.asValue().name + "\n");
            return;
        }

        step(item.asGraph().tail, builder);
        step(item.asGraph().head, builder);
    }
}
