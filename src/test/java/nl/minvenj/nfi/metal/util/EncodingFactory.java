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

import nl.minvenj.nfi.ddrx.encoding.ByteOrder;
import nl.minvenj.nfi.ddrx.encoding.Encoding;

public class EncodingFactory {

    public static Encoding enc() {
        return new Encoding();
    }

    public static Encoding signed() {
        return new Encoding(true);
    }

    public static Encoding le() {
        return new Encoding(ByteOrder.LITTLE_ENDIAN);
    }

}
