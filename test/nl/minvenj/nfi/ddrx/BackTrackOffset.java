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

import java.math.BigInteger;

import nl.minvenj.nfi.ddrx.expression.True;
import nl.minvenj.nfi.ddrx.expression.comparison.Equals;
import nl.minvenj.nfi.ddrx.expression.value.Con;
import nl.minvenj.nfi.ddrx.expression.value.Ref;
import nl.minvenj.nfi.ddrx.io.ByteStream;
import nl.minvenj.nfi.ddrx.token.Choice;
import nl.minvenj.nfi.ddrx.token.Sequence;
import nl.minvenj.nfi.ddrx.token.Token;
import nl.minvenj.nfi.ddrx.token.Value;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class BackTrackOffset {
	
	private Token _backTrack = new Choice(
			new Sequence(anyValue("a"), fixedValue("b", 2)),
			new Sequence(anyValue("c"), fixedValue("d", 3)));
	
	private Token anyValue(String name) {
		return new Value(name, new Con(BigInteger.valueOf(1)), new True());
	}
	
	private Token fixedValue(String name, int value) {
		return new Value(name, new Con(BigInteger.valueOf(1)), new Equals(new Ref(name), new Con(BigInteger.valueOf(value))));
	}
	
	@Test
	public void directMatchOffset1() {
		Assert.assertTrue(_backTrack.eval(new ByteStream(new byte[] { 1, 2 })));
	}
	
	@Test
	public void backTrackOffset1() {
		Assert.assertTrue(_backTrack.eval(new ByteStream(new byte[] { 1, 3 })));
	}
	
}
