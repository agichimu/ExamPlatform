
package undertowserver;

import Rest.Routes;
import Utilities.ConnectionsXmlReader;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.PathHandler;

import java.nio.charset.StandardCharsets;

import static Rest.RouteHandler.routeHandler;

public class UndertowTest {

    public static void main(String[] args) {
        routeHandler();

        System.out.println("Starting REST API");

        try {

            ConnectionsXmlReader.getPortRest();
            ConnectionsXmlReader.getHostRest();
            ConnectionsXmlReader.getBasePathRest();

           //Reading From Config

            String  portRest = ConnectionsXmlReader.getPortRest();
            String hostRest = ConnectionsXmlReader.getHostRest();
            String basePathRest = ConnectionsXmlReader.getBasePathRest();

            PathHandler routeHandler = Handlers.path().addPrefixPath(basePathRest + "/examinations", Routes.examinations());

            assert portRest != null;
            Undertow server = Undertow.builder()
                    .setServerOption(UndertowOptions.DECODE_URL, true)
                    .setServerOption(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name())
                    .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                    .addHttpListener(Integer.parseInt(portRest), hostRest)
                    .setHandler(routeHandler)
                    .build();

            server.start();

            System.out.println("Server Started at "+hostRest+":"+portRest);



        } catch (Exception e) {
            System.err.println("Failed to Start REST API");
            throw new RuntimeException(e);
        }

    }

}
