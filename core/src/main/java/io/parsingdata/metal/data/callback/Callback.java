package io.parsingdata.metal.data.callback;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.encoding.Encoding;

public interface Callback {

    void handle(final Environment env, final Encoding enc, final ParseItem struct);

}
