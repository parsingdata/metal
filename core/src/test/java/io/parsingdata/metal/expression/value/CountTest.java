package io.parsingdata.metal.expression.value;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.count;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import java.util.Arrays;
import java.util.Collection;

import org.junit.runners.Parameterized.Parameters;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.ParameterizedParse;

public class CountTest extends ParameterizedParse {

    private static final Token COUNT = seq(
        rep(def("a", 1, eq(con(3)))),
        def("count", 1, eq(count(ref("a"))))
    );

    @Parameters(name="{0} ({4})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
            {"[] = count(0)", COUNT, stream(0), enc(), true},
            {"[3] = count(1)", COUNT, stream(3, 1), enc(), true},
            {"[3,3] = count(2)", COUNT, stream(3, 3, 2), enc(), true},
            {"[3,3,3] = fail", COUNT, stream(3, 3, 3, 3), enc(), false}, // fails because the rep 'eats' the 4th '3'
            {"[3,3,3,3] = count(4)", COUNT, stream(3, 3, 3, 3, 4), enc(), true},
        });
    }

    public CountTest(final String desc, final Token token, final Environment env, final Encoding enc, final boolean result) {
        super(token, env, enc, result);
    }

}
