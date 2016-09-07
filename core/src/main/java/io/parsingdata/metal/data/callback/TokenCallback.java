package io.parsingdata.metal.data.callback;

import io.parsingdata.metal.token.Token;

import static io.parsingdata.metal.Util.checkNotNull;

public class TokenCallback {

    public final Token token;
    public final Callback callback;

    public TokenCallback(final Token token, final Callback callback) {
        this.token = checkNotNull(token, "token");
        this.callback = checkNotNull(callback, "callback");
    }

}
