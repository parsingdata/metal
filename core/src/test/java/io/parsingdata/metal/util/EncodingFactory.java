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

package io.parsingdata.metal.util;

import static io.parsingdata.metal.encoding.Encoding.DEFAULT_ENCODING;

import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.encoding.Sign;

public class EncodingFactory {

    public static Encoding enc() {
        return DEFAULT_ENCODING;
    }

    public static Encoding signed() {
        return new Encoding(Sign.SIGNED);
    }

    public static Encoding le() {
        return new Encoding(ByteOrder.LITTLE_ENDIAN);
    }

}
