package examinations;

import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public class DeleteExaminations {
    public static void main(String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(exchange -> {
                    if (exchange.getRequestMethod().equalToString("DELETE") && exchange.getRequestPath().equals("/exams")) {
                        // Handle DELETE request to delete exam data
                        handleDeleteExams(exchange);
                    } else {
                        exchange.setStatusCode(404);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send("Not Found");
                    }
                })
                .build();

        server.start();
    }

    public static void handleDeleteExams(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(() -> handleDeleteExams(exchange));
            return;
        }

        // Simulate deleting exam data
        String deleteResponse = "Exam data has been deleted.";

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send(deleteResponse);
    }
}
