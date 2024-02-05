package io.parsingdata.metal.expression.value.reference;

import static java.nio.charset.StandardCharsets.UTF_8;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eqStr;
import static io.parsingdata.metal.Shorthand.opt;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.rep;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.data.Selection.reverse;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Token;

class RefMultiReferencesTest {

    private static ParseState parseState;
    private static Token first;
    private static Token second;
    private static Token third;

    @BeforeAll
    public static void setup() {
        first = def("first", 1);
        second = def("second", 1);
        third = def("third", 1);
        final String input = "a1-b2-c3";
        final Optional<ParseState> parse = rep(seq("test", first, second, opt(post(third, eqStr(con("-")))))).parse(new Environment(stream(input, UTF_8), enc()));
        assertTrue(parse.isPresent());
        parseState = parse.get();
    }

    public static Stream<Arguments> multiNameRef() {
        return Stream.of(
            arguments(ref("first"), "abc"),
            arguments(ref("second"), "123"),
            arguments(ref("third"), "--"),
            arguments(ref("first", "second"), "a1b2c3"),
            arguments(ref("first", "third"), "a-b-c"),
            arguments(ref("second", "first"), "a1b2c3"),
            arguments(ref("second", "third"), "1-2-3"),
            arguments(ref("third", "first"), "a-b-c"),
            arguments(ref("third", "second"), "1-2-3"),
            arguments(ref("first", "second", "third"), "a1-b2-c3"),
            arguments(ref("second", "third", "first"), "a1-b2-c3"),
            arguments(ref("third", "first", "second"), "a1-b2-c3")
        );
    }

    @ParameterizedTest
    @MethodSource
    public void multiNameRef(final ValueExpression ref, final String expected) {
        assertLists(expected, ref.eval(parseState, enc()));
    }


    public static Stream<Arguments> multiDefinitionRef() {
        return Stream.of(
            arguments(ref(first), "abc"),
            arguments(ref(second), "123"),
            arguments(ref(third), "--"),
            arguments(ref(first, second), "a1b2c3"),
            arguments(ref(first, third), "a-b-c"),
            arguments(ref(second, first), "a1b2c3"),
            arguments(ref(second, third), "1-2-3"),
            arguments(ref(third, first), "a-b-c"),
            arguments(ref(third, second), "1-2-3"),
            arguments(ref(first, second, third), "a1-b2-c3"),
            arguments(ref(second, third, first), "a1-b2-c3"),
            arguments(ref(third, first, second), "a1-b2-c3")
        );
    }

    @ParameterizedTest
    @MethodSource
    public void multiDefinitionRef(final ValueExpression ref, final String expected) {
        assertLists(expected, ref.eval(parseState, enc()));
    }

    private void assertLists(final String expected, final ImmutableList<Value> result) {
        assertEquals(expected.length(), (long) result.size());
        ImmutableList<Value> tail = reverse(result);
        for (int i = 0; i < expected.length(); i++) {
            assertNotNull(tail);
            assertNotNull(tail.head());
            assertEquals(expected.charAt(i), tail.head().value()[0]);
            tail = tail.tail();
        }
    }

}