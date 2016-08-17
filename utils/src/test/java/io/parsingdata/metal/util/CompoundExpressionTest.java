package io.parsingdata.metal.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.expTrue;
import static io.parsingdata.metal.util.CompoundExpression.expFalse;
import static io.parsingdata.metal.util.CompoundExpression.gtEqNum;
import static io.parsingdata.metal.util.CompoundExpression.imp;
import static io.parsingdata.metal.util.CompoundExpression.ltEqNum;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;

import org.junit.Test;

public class CompoundExpressionTest {

    @Test
    public void testExpFalse() {
        final boolean value = expFalse().eval(stream(), enc());
        assertThat(value, is(equalTo(false)));
    }

    @Test
    public void testGtEqNumWhenLess() {
        final boolean value = gtEqNum(con(1), con(2)).eval(stream(), enc());
        assertThat(value, is(equalTo(false)));
    }

    @Test
    public void testGtEqNumWhenEqual() {
        final boolean value = gtEqNum(con(1), con(1)).eval(stream(), enc());
        assertThat(value, is(equalTo(true)));
    }

    @Test
    public void testGtEqNumWhenMore() {
        final boolean value = gtEqNum(con(2), con(1)).eval(stream(), enc());
        assertThat(value, is(equalTo(true)));
    }

    @Test
    public void testGtEqNumAgainstNullCurrentValue() {
        final boolean value = gtEqNum(con(1)).eval(stream(), enc());
        assertThat(value, is(equalTo(false)));
    }

    @Test
    public void testLtEqNumWhenLess() {
        final boolean value = ltEqNum(con(1), con(2)).eval(stream(), enc());
        assertThat(value, is(equalTo(true)));
    }

    @Test
    public void testLtEqNumWhenEqual() {
        final boolean value = ltEqNum(con(1), con(1)).eval(stream(), enc());
        assertThat(value, is(equalTo(true)));
    }

    @Test
    public void testLtEqNumWhenMore() {
        final boolean value = ltEqNum(con(2), con(1)).eval(stream(), enc());
        assertThat(value, is(equalTo(false)));
    }

    @Test
    public void testLtEqNumAgainstNullCurrentValue() {
        final boolean value = ltEqNum(con(1)).eval(stream(), enc());
        assertThat(value, is(equalTo(false)));
    }

    @Test
    public void testImplicationTrueTrue() {
        final boolean value = imp(expTrue(), expTrue()).eval(stream(), enc());
        assertThat(value, is(equalTo(true)));
    }

    @Test
    public void testImplicationTrueFalse() {
        final boolean value = imp(expTrue(), expFalse()).eval(stream(), enc());
        assertThat(value, is(equalTo(false)));
    }

    @Test
    public void testImplicationFalseTrue() {
        final boolean value = imp(expFalse(), expTrue()).eval(stream(), enc());
        assertThat(value, is(equalTo(true)));
    }

    @Test
    public void testImplicationFalseFalse() {
        final boolean value = imp(expFalse(), expFalse()).eval(stream(), enc());
        assertThat(value, is(equalTo(true)));
    }
}
