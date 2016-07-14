package io.parsingdata.metal.data;

import io.parsingdata.metal.token.Token;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ParseValueTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Token _definition;
    private ParseValue _value;

    @Before
    public void setUp() {
        _definition = def("value", 1);
        _value = new ParseValue("scope", "value", _definition, 0, new byte[] { 1 }, enc());
    }

    @Test
    public void state() {
        assertThat(_value.getScope(), is("scope"));
        assertThat(_value.getName(), is("value"));
        assertThat(_value.getFullName(), is("scope.value"));
        assertThat(_value.getDefinition(), is(_definition));
        assertThat(_value.getOffset(), is(0L));
        assertThat(_value.getValue(), is(equalTo(new byte[] { 1 })));
    }

    @Test
    public void matching() {
        assertTrue(_value.matches("value"));
        assertTrue(_value.matches("scope.value"));

        assertFalse(_value.matches("lue"));
        assertFalse(_value.matches(".value"));
        assertFalse(_value.matches("cope.value"));
    }

    @Test
    public void toStringTest() {
        assertThat(_value.toString(), is("value(0x01)"));
    }

    @Test
    public void valueIsAValue() {
        assertTrue(_value.isValue());
        assertThat(_value.asValue(), is(sameInstance(_value)));
    }

    @Test
    public void valueIsNotARef() {
        assertFalse(_value.isRef());

        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Cannot convert ParseValue to ParseRef");
        _value.asRef();
    }

    @Test
    public void valueIsNotAGraph() {
        assertFalse(_value.isGraph());

        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Cannot convert ParseValue to ParseGraph");
        _value.asGraph();
    }

}