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

package nl.minvenj.nfi.metal;

import static nl.minvenj.nfi.metal.Shorthand.con;
import static nl.minvenj.nfi.metal.Shorthand.def;
import static nl.minvenj.nfi.metal.Shorthand.seq;
import static nl.minvenj.nfi.metal.util.EncodingFactory.enc;
import static nl.minvenj.nfi.metal.util.EnvironmentFactory.stream;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import nl.minvenj.nfi.metal.data.ParseGraph;
import nl.minvenj.nfi.metal.data.ParseResult;
import nl.minvenj.nfi.metal.token.Token;

public class DefinitionTest {
	
	// TODO: METAL-41: Add a bunch of additional tests here to check whether all Token types work correctly.
	
	@Test
	public void singleDef() throws IOException {
		final Token singleDef = def("a", con(1));
		final ParseResult res = singleDef.parse(stream(1), enc());
		Assert.assertTrue(res.succeeded());
		Assert.assertTrue(res.getEnvironment().order.getDefinition() == ParseGraph.NONE);
		Assert.assertTrue(res.getEnvironment().order.head.getDefinition() == singleDef);
		Assert.assertTrue(res.getEnvironment().order.tail.getDefinition() == ParseGraph.NONE);
	}

	@Test
	public void smallSeq() throws IOException {
		Token defA = def("a", con(1));
		Token defB = def("b", con(1));
		final Token smallSeq = seq(defA, defB);
		final ParseResult res = smallSeq.parse(stream(1, 2), enc());
		Assert.assertTrue(res.succeeded());
		Assert.assertTrue(res.getEnvironment().order.getDefinition() == ParseGraph.NONE);
		Assert.assertTrue(res.getEnvironment().order.head.getDefinition() == smallSeq);
		Assert.assertTrue(res.getEnvironment().order.head.asGraph().head.getDefinition() == defB);
		Assert.assertTrue(res.getEnvironment().order.head.asGraph().tail.getDefinition() == smallSeq);
		Assert.assertTrue(res.getEnvironment().order.head.asGraph().tail.head.getDefinition() == defA);
		Assert.assertTrue(res.getEnvironment().order.head.asGraph().tail.tail.getDefinition() == smallSeq);
		Assert.assertTrue(res.getEnvironment().order.tail.getDefinition() == ParseGraph.NONE);
	}

}
