package Rest;

import examinations.CreateExaminations;
import examinations.DeleteExaminations;
import examinations.ReadExaminations;
import examinations.UpdateExaminations;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;
import io.undertow.util.Headers;

public class RouteHandler {

    public static void main(String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(createRoutingHandler())
                .build();

        server.start();
    }

    private static RoutingHandler createRoutingHandler() {
        RoutingHandler routingHandler = Handlers.routing();

        // Exam-related routes
        routingHandler.add("POST",
                "/exams",
                CreateExaminations::handleCreateExam);

        routingHandler.add("GET", "/exams/", ReadExaminations::readExams);

        routingHandler.add("PUT", "/exams/1", UpdateExaminations::updateExams);

        routingHandler.add("DELETE", "/exams/1", DeleteExaminations::handleDeleteExams);

        /*// User-related routes

        routingHandler.add("POST", "/users", exchange -> {
            CreateUserController.handleCreateUser(exchange);
        });

        routingHandler.add("GET", "/users", exchange -> {
            ReadUserController.handleReadUsers(exchange);
        });

        routingHandler.add("PUT", "/users/{userId}", exchange -> {
            UpdateUserController.handleUpdateUser(exchange);
        });

        routingHandler.add("DELETE", "/users/{userId}", exchange -> {
            DeleteUserController.handleDeleteUser(exchange);
        });*/

        // Not Found handler
        routingHandler.setFallbackHandler(exchange -> {
            exchange.setStatusCode(404);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "Application/JSON");
            exchange.getResponseSender().send("Not Found");
        });

        return routingHandler;
    }
}
