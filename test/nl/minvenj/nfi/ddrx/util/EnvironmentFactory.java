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
import java.nio.file.Files;
import java.nio.file.Path;

import nl.minvenj.nfi.ddrx.data.Environment;
import nl.minvenj.nfi.ddrx.encoding.Encoding;

public class EnvironmentFactory {

    public static Environment stream(int... bytes) {
        return stream(new Encoding(), bytes);
    }

    public static Environment stream(Encoding e, int... bytes) {
        return new Environment(e, new InMemoryByteStream(toByteArray(bytes)));
    }

    public static Environment stream(Encoding e, Path path) throws IOException {
        return new Environment(e, new InMemoryByteStream(Files.readAllBytes(path)));
    }

    public static Environment stream(Path path) throws IOException {
        return stream(new Encoding(), path);
    }

    public static Environment stream(Encoding e, String value) {
        return new Environment(e, new InMemoryByteStream(value.getBytes(e.getCharset())));
    }

    public static Environment stream(String value) {
        return stream(new Encoding(), value);
    }

    public static byte[] toByteArray(int... bytes) {
        final byte[] out = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            out[i] = (byte) bytes[i];
        }
        return out;
    }

}
