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

import static nl.minvenj.nfi.ddrx.util.EncodingFactory.enc;
import static nl.minvenj.nfi.ddrx.util.EncodingFactory.le;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import nl.minvenj.nfi.ddrx.expression.value.ConstantFactory;

@RunWith(JUnit4.class)
public class Constants {

    @Test
    public void numericBigEndian() {
        Assert.assertEquals(1764, ConstantFactory.createFromNumeric(BigInteger.valueOf(1764), enc()).asNumeric().longValue());
    }

    @Test
    public void numericLittleEndian() {
        Assert.assertEquals(1764, ConstantFactory.createFromNumeric(BigInteger.valueOf(1764), le()).asNumeric().longValue());
    }

    @Test
    public void numericEdgeCase() {
        Assert.assertEquals(256, ConstantFactory.createFromNumeric(BigInteger.valueOf(256), le()).asNumeric().longValue());
    }

}
