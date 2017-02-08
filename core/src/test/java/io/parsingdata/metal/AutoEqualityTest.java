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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.expTrue;
import static io.parsingdata.metal.Shorthand.not;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EncodingFactory.le;
import static io.parsingdata.metal.util.EncodingFactory.signed;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.value.ValueExpression;
import io.parsingdata.metal.token.Cho;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.Token;

@RunWith(Parameterized.class)
public class AutoEqualityTest {

    private static final List<Object> STRINGS = new ArrayList<Object>() {{ add("a"); add("b"); }};
    private static final List<Object> ENCODINGS = new ArrayList<Object>() {{ add(enc()); add(signed()); add(le()); }};
    private static final List<Object> TOKENS = new ArrayList<Object>() {{ add(any("a")); add(any("b")); }};
    private static final List<Object> TOKEN_ARRAYS = new ArrayList<Object>() {{ add(new Token[] { any("a"), any("b")}); add(new Token[] { any("b"), any("c") }); add(new Token[] { any("a"), any("b"), any("c") }); }};
    private static final List<Object> VALUE_EXPRESSIONS = new ArrayList<Object>() {{ add(con(1)); add(con(2)); }};
    private static final List<Object> EXPRESSIONS = new ArrayList<Object>() {{ add(expTrue()); add(not(expTrue())); }};
    private static final Map<Class, List<Object>> mapping = new HashMap<Class, List<Object>>() {{
        put(String.class, STRINGS);
        put(Encoding.class, ENCODINGS);
        put(Token.class, TOKENS);
        put(Token[].class, TOKEN_ARRAYS);
        put(ValueExpression.class, VALUE_EXPRESSIONS);
        put(Expression.class, EXPRESSIONS);
    }};

    @Parameterized.Parameters(name="{0}")
    public static Collection<Object[]> data() {
        return generateObjectArrays(Cho.class, Def.class);
    }

    private static Collection<Object[]> generateObjectArrays(Class... classes) {
        Collection<Object[]> results = new ArrayList<>();
        for (Class c : classes) {
            results.addAll(generateObjectArrays(c));
        }
        return results;
    }

    private static List<Object[]> generateObjectArrays(Class c) {
        Constructor cons = c.getConstructors()[0];
        List<List<Object>> args = new ArrayList<>();
        for (Class cl : cons.getParameterTypes()) {
            args.add(mapping.get(cl));
        }
        List<List<Object>> argLists = generateCombinations(0, args);
        List<Object[]> instanceLists = new ArrayList<>();
        for (int i = 0; i < argLists.size(); i++) {
            Object[] instances = new Object[3];
            try {
                instances[0] = cons.newInstance(argLists.get(i).toArray());
                instances[1] = cons.newInstance(argLists.get(i).toArray());
                List<Object> otherInstances = new ArrayList<>();
                for (List<Object> argList : argLists.subList(i, argLists.size())) {
                    otherInstances.add(cons.newInstance(argList.toArray()));
                }
                instances[2] = otherInstances.toArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instanceLists.add(instances);
        }
        return instanceLists;
    }

    private static List<List<Object>> generateCombinations(int index, List<List<Object>> args) {
        List<List<Object>> result = new ArrayList<>();
        if (index == args.size()) {
            result.add(new ArrayList<>());
        } else {
            for (Object obj : args.get(index)) {
                for (List<Object> list : generateCombinations(index + 1, args)) {
                    list.add(0, obj);
                    result.add(list);
                }
            }
        }
        return result;
    }

    private final Object object;
    private final Object same;
    private final Object[] other;

    public AutoEqualityTest(final Object object, final Object same, final Object[] other) throws NoSuchMethodException {
        this.object = object;
        this.same = same;
        this.other = other;
    }

    @Test
    public void NotEqualsNull() {
        assertFalse(object.equals(null));
        assertFalse(other.equals(null));
    }

    @Test
    public void equalsItselfIdentity() {
        assertTrue(object.equals(object));
        assertTrue(other.equals(other));
    }

    @Test
    public void equalsItself() {
        assertTrue(object.equals(same));
        assertTrue(same.equals(object));
    }

}
