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

package nl.minvenj.nfi.ddrx.util;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import nl.minvenj.nfi.ddrx.data.Environment;

public class EnvironmentFactory {

    public static Environment stream(int... bytes) {
        return new Environment(new InMemoryByteStream(toByteArray(bytes)));
    }

    public static Environment stream(URI resource) throws IOException {
        return new Environment(new InMemoryByteStream(Files.readAllBytes(Paths.get(resource))));
    }

    public static Environment stream(String value, Charset charset) {
        return new Environment(new InMemoryByteStream(value.getBytes(charset)));
    }

    public static byte[] toByteArray(int... bytes) {
        final byte[] out = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            out[i] = (byte) bytes[i];
        }
        return out;
    }

}
