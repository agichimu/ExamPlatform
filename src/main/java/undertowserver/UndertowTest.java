package undertowserver;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

public class UndertowTest {

    public static void main(String[] args) {

        String BASE_URL = "/api/rest";


        /* HTTP handler */
        HttpHandler handler = exchange -> exchange.getResponseSender().send("Hello, Undertow!");

       /* PathHandler pathHandler = Handlers.path()
                .addPrefixPath(BASE_URL + "/examinations", Routes.examinations());
*/
        /* Create an Undertow server and add the handler */

        System.out.println("Starting Server");
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(handler)
                .build();

        /* Start the server */
        server.start();
    }
    public static void Pathhandler(){
        System.out.println("1");

    }
}
