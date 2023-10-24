package undertowserver;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

public class UndertowTest {
    public static void main(String[] args) {
        /* Define a simple HTTP handler */
        HttpHandler handler = exchange -> exchange.getResponseSender().send("Hello, Undertow!");

        /* Create an Undertow server and add the handler */
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(handler)
                .build();

        /* Start the server */
        server.start();
    }
}
