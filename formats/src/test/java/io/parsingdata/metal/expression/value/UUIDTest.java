/*
 * Copyright 2013-2024 Netherlands Forensic Institute
 * Copyright 2021-2024 Infix Technologies B.V.
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

package io.parsingdata.metal.expression.value;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.expression.value.UUID.uuid;
import static io.parsingdata.metal.util.ClassDefinition.checkUtilityClass;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.token.Token;

public class UUIDTest {

    @Test
    public void checkUtility() throws ReflectiveOperationException {
        checkUtilityClass(UUID.class);
    }

    @Test
    public void parseUUID() {
        final Token uuid = def("uuid", 16, eq(uuid("00c27766-f623-4200-9d64-115e9bfd4a08")));
        assertTrue(uuid.parse(env(stream(0x00, 0xc2, 0x77, 0x66, 0xf6, 0x23, 0x42, 0x00, 0x9d, 0x64, 0x11, 0x5e, 0x9b, 0xfd, 0x4a, 0x08))).isPresent());
        assertFalse(uuid.parse(env(stream(0x66, 0x77, 0xc2, 0x00, 0x23, 0xf6, 0x00, 0x42, 0x9d, 0x64, 0x11, 0x5e, 0x9b, 0xfd, 0x4a, 0x08))).isPresent());
    }
}
