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

public final class ParseItem {

    private final ParseValue _pv;
    private final ParseGraph _pg;
    private final long _ref;

    public ParseItem(final ParseValue pv) {
        if (pv == null) { throw new IllegalArgumentException("Argument pv may not be null."); }
        _pv = pv;
        _pg = null;
        _ref = 0;
    }

    public ParseItem(final ParseGraph pg) {
        if (pg == null) { throw new IllegalArgumentException("Argument pg may not be null."); }
        _pg = pg;
        _pv = null;
        _ref = 0;
    }

    public ParseItem(final long ref) {
        _pg = null;
        _pv = null;
        _ref = ref;
    }

    public boolean isValue() { return _pv != null; }
    public boolean isGraph() { return _pg != null; }
    public boolean isRef() { return _pv == null && _pg == null; }

    public ParseValue getValue() {
        if (!isValue()) { throw new IllegalStateException("This ParseItem does not contain a ParseValue."); }
        return _pv;
    }

    public ParseGraph getGraph() {
        if (!isGraph()) { throw new IllegalStateException("This ParseItem does not contain a ParseGraph."); }
        return _pg;
    }

    public ParseGraph getRef(final ParseGraph root) {
        if (!isRef()) { throw new IllegalStateException("This ParseItem does not contain a Reference."); }
        return ParseGraph.findRef(ParseGraphList.create(root).add(root.getGraphs()), _ref);
    }
    
    @Override
    public String toString() {
        return (isValue() ? "ParseValue(" + _pv.toString() : (isGraph() ? "ParseGraph(" + _pg.toString() : "Ref(" + _ref)) + ")";
    }

}
