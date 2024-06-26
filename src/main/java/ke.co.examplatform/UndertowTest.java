package ke.co.examplatform;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.PathHandler;
import ke.co.examplatform.Rest.Routes;
import ke.co.examplatform.Utilities.ConnectionsXmlReader;

import java.nio.charset.StandardCharsets;

import static ke.co.examplatform.Utilities.ConnectionsXmlReader.getDbConnection;

public class UndertowTest {
    public static void main(String[] args) throws RuntimeException {getDbConnection();
        System.out.println("Starting REST API");

        try {
            String portRest = ConnectionsXmlReader.getPortRest();
            String hostRest = ConnectionsXmlReader.getHostRest();
            String basePathRest = ConnectionsXmlReader.getBasePathRest();

            try {
                PathHandler routeHandler = Handlers.path()
                        .addPrefixPath(basePathRest + "/management/departments", Routes.Departments())
                        .addPrefixPath(basePathRest + "/management/classes", Routes.Classes())
                        .addPrefixPath(basePathRest + "/management/subjects", Routes.Subjects())
                        .addPrefixPath(basePathRest + "/examinations", Routes.examinations())
                        .addPrefixPath(basePathRest + "/examinations/questions", Routes.Questions())
                        .addPrefixPath(basePathRest + "/examinations/questions/choices", Routes.Choices())
                        .addPrefixPath(basePathRest + "/users/pupils", Routes.Pupils())
                        .addPrefixPath(basePathRest + "/users/teachers", Routes.Teachers())
                        .addPrefixPath(basePathRest + "/users/guardians", Routes.Guardians())
                        .addPrefixPath(basePathRest + "/users/guardians/roles", Routes.GuardianRoles())
                        .addPrefixPath(basePathRest + "/users/genders", Routes.Genders());


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
            System.err.println("Error starting the server.");
        }
    }
}