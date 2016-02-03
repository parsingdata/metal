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

import static nl.minvenj.nfi.metal.Util.checkNotNull;

public final class ParseItem {

    private final ParseValue _pv;
    private final ParseGraph _pg;
    private final ParseRef _pr;

    public ParseItem(final ParseValue pv) {
        _pv = checkNotNull(pv, "pv");
        _pg = null;
        _pr = null;
    }

    public ParseItem(final ParseGraph pg) {
        _pg = checkNotNull(pg, "pg");
        _pv = null;
        _pr = null;
    }

    public ParseItem(final ParseRef ref) {
        _pg = null;
        _pv = null;
        _pr = ref;
    }

    public boolean isValue() { return _pv != null; }
    public boolean isGraph() { return _pg != null; }
    public boolean isRef() { return _pr != null; }

    public ParseValue getValue() {
        if (!isValue()) { throw new IllegalStateException("This ParseItem does not contain a ParseValue."); }
        return _pv;
    }

    public ParseGraph getGraph() {
        if (!isGraph()) { throw new IllegalStateException("This ParseItem does not contain a ParseGraph."); }
        return _pg;
    }

    public ParseRef getRef() {
        if (!isRef()) { throw new IllegalStateException("This ParseItem does not contain a Reference."); }
        return _pr;
    }

    @Override
    public String toString() {
        return (isValue() ? "ParseValue(" + _pv.toString() : (isGraph() ? "ParseGraph(" + _pg.toString() : "Ref(" + _pr)) + ")";
    }

}
