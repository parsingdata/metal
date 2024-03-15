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

package io.parsingdata.metal;

import static io.parsingdata.metal.util.ClassDefinition.checkUtilityClass;

import org.junit.jupiter.api.Test;

import io.parsingdata.metal.format.Callback;
import io.parsingdata.metal.format.JPEG;
import io.parsingdata.metal.format.PNG;
import io.parsingdata.metal.format.VarInt;
import io.parsingdata.metal.format.ZIP;

public class DescriptionClassTest {

    // Check that description and callback classes are well-formed.
    @Test
    public void utilityClasses() throws ReflectiveOperationException {
        checkUtilityClass(Callback.class);
        checkUtilityClass(JPEG.class);
        checkUtilityClass(PNG.class);
        checkUtilityClass(ZIP.class);
        checkUtilityClass(VarInt.class);
    }

}
