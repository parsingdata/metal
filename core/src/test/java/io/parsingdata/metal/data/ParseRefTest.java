package io.parsingdata.metal.data;

import static io.parsingdata.metal.Shorthand.*;
import static junit.framework.TestCase.assertFalse;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import io.parsingdata.metal.token.Token;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ParseRefTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private Token _definition;
    private ParseRef _ref;

    @Before
    public void setUp() {
        _definition = sub(def("value", 1), con(0));
        _ref = new ParseRef(0L, _definition);
    }

    @Test
    public void state() {
        assertThat(_ref.location, is(0L));
        assertThat(_ref.getDefinition(), is(_definition));
    }

    @Test
    public void toStringTest() {
        assertThat(_ref.toString(), is("ParseRef(0)"));
    }

    @Test
    public void refIsARef() {
        assertTrue(_ref.isRef());
        assertThat(_ref.asRef(), is(sameInstance(_ref)));
    }

    @Test
    public void refIsNotAValue() {
        assertFalse(_ref.isValue());

        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Cannot convert ParseRef to ParseValue");
        _ref.asValue();
    }

    @Test
    public void refIsNotAGraph() {
        assertFalse(_ref.isGraph());

        thrown.expect(UnsupportedOperationException.class);
        thrown.expectMessage("Cannot convert ParseRef to ParseGraph");
        _ref.asGraph();
    }

}