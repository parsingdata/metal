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

package nl.minvenj.nfi.ddrx;

import static nl.minvenj.nfi.ddrx.Shorthand.cat;
import static nl.minvenj.nfi.ddrx.Shorthand.con;
import static nl.minvenj.nfi.ddrx.Shorthand.defNum;
import static nl.minvenj.nfi.ddrx.Shorthand.defStr;
import static nl.minvenj.nfi.ddrx.Shorthand.defVal;
import static nl.minvenj.nfi.ddrx.Shorthand.eq;
import static nl.minvenj.nfi.ddrx.Shorthand.expTrue;
import static nl.minvenj.nfi.ddrx.Shorthand.not;
import static nl.minvenj.nfi.ddrx.Shorthand.refNum;
import static nl.minvenj.nfi.ddrx.Shorthand.refVal;
import static nl.minvenj.nfi.ddrx.Shorthand.rep;
import static nl.minvenj.nfi.ddrx.Shorthand.seq;
import static nl.minvenj.nfi.ddrx.data.Environment.stream;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.CRC32;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.expression.value.NumericValue;
import nl.minvenj.nfi.ddrx.expression.value.UnaryValueExpression;
import nl.minvenj.nfi.ddrx.expression.value.Value;
import nl.minvenj.nfi.ddrx.expression.value.ValueOperation;
import nl.minvenj.nfi.ddrx.token.Token;

@RunWith(JUnit4.class)
public class PNG {
    
    private static final Path FILE = Paths.get("testdata/test.png");
    
    private static final Token PNG_HEADER = seq(defVal("highbit", con(1), eq(con(0x89))),
                                                seq(defStr("PNG", con(3), eq(con("PNG"))),
                                                    defVal("controlchars", con(4), eq(con(0x0d0a1a0a)))));
    private static final Token PNG_FOOTER = seq(defNum("footerlength", con(4), eq(con(0))),
                                                seq(defStr("footertype", con(4), eq(con("IEND"))),
                                                    defVal("footercrc32", con(4), eq(con(0xae426082)))));
    private static final Token PNG_STRUCT = seq(defNum("length", con(4), expTrue()),
                                                seq(defStr("chunktype", con(4), not(eq(con("IEND")))),
                                                    seq(defVal("chunkdata", refNum("length"), expTrue()),
                                                        defVal("crc32", con(4), eq(new UnaryValueExpression<Value>(cat(refVal("chunktype"), refVal("chunkdata"))) {
                                                            @Override
                                                            public Value eval(Environment env) {
                                                                return _op.eval(env).operation(new ValueOperation() {
                                                                    @Override
                                                                    public Value execute(byte[] value) {
                                                                        CRC32 crc = new CRC32();
                                                                        crc.update(value);
                                                                        return new NumericValue(BigInteger.valueOf(crc.getValue()));
                                                                    }});
                                                            }})))));
    private static final Token PNG = seq(PNG_HEADER,
                                         seq(rep(PNG_STRUCT),
                                             PNG_FOOTER));
    
    @Test
    public void parsePNG() throws IOException {
        Assert.assertTrue(PNG.parse(stream(FILE)));
    }
    
}
