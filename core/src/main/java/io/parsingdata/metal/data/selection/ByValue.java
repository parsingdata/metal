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

package io.parsingdata.metal.data.selection;

import io.parsingdata.metal.data.ImmutableList;
import io.parsingdata.metal.data.ParseValue;

public final class ByValue {

    private ByValue() {}

    public static ImmutableList<ParseValue> getValuesSincePrefix(final ImmutableList<ParseValue> list, final ParseValue prefix) {
        if (list.isEmpty()) { return list; }
        if (list.head == prefix) { return new ImmutableList<ParseValue>(); }
        final ImmutableList<ParseValue> tailList = getValuesSincePrefix(list.tail, prefix);
        return tailList.add(list.head);
    }

}
