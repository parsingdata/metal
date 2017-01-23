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

import static org.junit.Assert.assertEquals;

import static io.parsingdata.metal.Util.tokensToString;
import static io.parsingdata.metal.util.ClassDefinition.checkUtilityClass;

import java.io.IOException;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.selection.ByItem;
import io.parsingdata.metal.data.selection.ByName;
import io.parsingdata.metal.data.selection.ByOffset;
import io.parsingdata.metal.data.selection.ByToken;
import io.parsingdata.metal.data.selection.ByType;
import io.parsingdata.metal.data.transformation.Reversal;
import io.parsingdata.metal.data.transformation.Wrapping;
import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.encoding.Sign;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.token.Token;

public class UtilityClassTest {

    // Check that utility classes are well-formed.
    @Test
    public void utilityClasses() throws ReflectiveOperationException {
        checkUtilityClass(Shorthand.class);
        checkUtilityClass(Util.class);
        checkUtilityClass(Reversal.class);
        checkUtilityClass(ByItem.class);
        checkUtilityClass(ByName.class);
        checkUtilityClass(ByOffset.class);
        checkUtilityClass(ByToken.class);
        checkUtilityClass(ByType.class);
        checkUtilityClass(ConstantFactory.class);
        checkUtilityClass(Wrapping.class);
    }

    // The method is used to print tokens for composing Tokens' toString()
    // implementation. Since they all require a non-zero amount of tokens, this
    // method must be explicitly tested.
    @Test
    public void zeroTokensToString() {
        assertEquals("", tokensToString(new Token[] {}));
    }

    // Check whether all names are actually returned, to make sure there are no
    // off-by-one errors or similar.
    @Test
    public void checkAllTokensPrinted() {
        class NameToken extends Token {
            NameToken(final String name) { super(name, null); }
            @Override protected ParseResult parseImpl(final String scope, final Environment environment, final Encoding encoding) throws IOException { return null; }
            @Override public String toString() { return name; }
        }
        final NameToken aToken = new NameToken("a");
        final NameToken bToken = new NameToken("b");
        final NameToken cToken = new NameToken("c");
        assertEquals("a", tokensToString(new Token[] { aToken }));
        assertEquals("a, b", tokensToString(new Token[] { aToken, bToken }));
        assertEquals("a, b, c", tokensToString(new Token[] { aToken, bToken, cToken }));
    }

    // Metal uses enums to prevent the use of difficult to understand boolean arguments.
    // However, enums come with some inherited methods that are not of use internally.
    @Test
    public void inheritedEnumMethods() {
        assertEquals(2, Sign.values().length);
        assertEquals(Sign.SIGNED, Sign.valueOf("SIGNED"));
        assertEquals(Sign.UNSIGNED, Sign.valueOf("UNSIGNED"));
        assertEquals(2, ByteOrder.values().length);
        assertEquals(ByteOrder.BIG_ENDIAN, ByteOrder.valueOf("BIG_ENDIAN"));
        assertEquals(ByteOrder.LITTLE_ENDIAN, ByteOrder.valueOf("LITTLE_ENDIAN"));
    }

}
