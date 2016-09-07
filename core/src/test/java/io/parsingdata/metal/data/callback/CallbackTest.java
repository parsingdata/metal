package io.parsingdata.metal.data.callback;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;
import org.junit.Test;

import java.io.IOException;

import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.TokenDefinitions.any;
import static org.junit.Assert.assertTrue;

public class CallbackTest {

    @Test
    public void testHandleCallback() throws IOException {
        final Token token = any("a");
        final Token sequence = seq(token, token);
        final Callback callback = new Callback() {
            @Override
            public void handle(Environment env, Encoding enc, ParseItem struct) {
                System.out.print("Handled: ");
                if (struct.isGraph()) {
                    System.out.println("Graph: " + struct.asGraph());
                } else if (struct.isRef()) {
                    System.out.println("Ref: " + struct.asRef());
                } else if (struct.isValue()) {
                    System.out.println("Value: " + struct.asValue());
                }
            }
        };
        final TokenCallbackList callbacks = TokenCallbackList.create(new TokenCallback(token, callback)).add(new TokenCallback(sequence, callback));
        final Environment env = new Environment(new InMemoryByteStream(new byte[] { 0, 0 }), callbacks);
        assertTrue(sequence.parse(env, enc()).succeeded);
    }

}
