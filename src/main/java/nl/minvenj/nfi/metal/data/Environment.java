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

package nl.minvenj.nfi.metal.data;

public class Environment {

    public final ParseGraph order;
    public final ByteStream input;
    public final long offset;

    public Environment(final ParseGraph order, final ByteStream input, final long offset) {
        this.order = order;
        this.input = input;
        this.offset = offset;
    }

    public Environment(final ByteStream input, final long offset) {
        this(ParseGraph.EMPTY, input, offset);
    }

    public Environment(final ByteStream input) {
        this(ParseGraph.EMPTY, input, 0L);
    }

    @Override
    public String toString() {
        return "stream: " + input + "; offset: " + offset + "; order: " + order;
    }

}
