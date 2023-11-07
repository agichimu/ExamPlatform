package undertowserver;

import Rest.Routes;
import Utilities.ConnectionsXmlReader;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.PathHandler;

import java.nio.charset.StandardCharsets;

@SuppressWarnings("CallToPrintStackTrace")
public class UndertowTest {
    public static void main(String[] args) throws RuntimeException {
        System.out.println("Starting REST API");

        try {
            String portRest = ConnectionsXmlReader.getPortRest();
            String hostRest = ConnectionsXmlReader.getHostRest();
            String basePathRest = ConnectionsXmlReader.getBasePathRest();

            try {
                PathHandler routeHandler = Handlers.path()
                        .addPrefixPath(basePathRest + "/examinations", Routes.examinations())
                        .addPrefixPath(basePathRest + "/examinations/questions", Routes.Questions())
                        .addPrefixPath(basePathRest + "/examinations/questions/choices", Routes.Choices())
                        .addPrefixPath(basePathRest + "/users/pupils", Routes.Pupils())
                        .addPrefixPath(basePathRest + "/users/teachers", Routes.Teachers())
                        .addPrefixPath(basePathRest + "/users/guardians", Routes.Guardians());


                Undertow server = Undertow.builder()
                        .setServerOption(UndertowOptions.DECODE_URL, true)
                        .setServerOption(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name())
                        .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                        .addHttpListener(Integer.parseInt(portRest), hostRest)
                        .setHandler(routeHandler)
                        .build();

                server.start();

                System.out.println("Server Started at " + hostRest + ":" + portRest);
            } catch (Exception e) {
                System.err.println("Failed to Start REST API");
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in establishing the database connection or starting the server.");
        }
    }
}
