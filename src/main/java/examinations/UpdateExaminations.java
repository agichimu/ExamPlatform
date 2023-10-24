package examinations;

import Utilities.ConnectionsXmlReader;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UpdateExaminations {
    public static void main(String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(exchange -> {
                    if (exchange.getRequestMethod().equalToString("PUT") && exchange.getRequestPath().equals("/exams")) {
                        updateExams(exchange);
                    } else {
                        exchange.setStatusCode(404);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                        exchange.getResponseSender().send("Not Found");
                    }
                })
                .build();

        server.start();
    }

    public static void updateExams(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(() -> updateExams(exchange));
            return;
        }

        // Initialize the updated exam data
        String updatedExamData = "Exam data updated successfully";

        try {
            String databaseURL = ConnectionsXmlReader.getDatabaseURL();
            String dbName = ConnectionsXmlReader.getDatabaseName();
            String username = ConnectionsXmlReader.getUsername();
            String password = ConnectionsXmlReader.getPassword();

            String jdbcUrl = databaseURL + dbName;

            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {

                String updateQuery = "UPDATE examination_details SET instructions = ? WHERE examination_id = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                    /* Setting  the parameters */
                    preparedStatement.setString(1, "Do not Answer Any Question");
                    preparedStatement.setInt(2, 5); // Update exam with ID 1


                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        updatedExamData = "Exam data updated successfully";
                    } else {
                        updatedExamData = "Failed to update the exam data";
                    }
                }
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.setStatusCode(200); // HTTP 200 OK
            exchange.getResponseSender().send(updatedExamData);
        } catch (Exception e) {
            e.printStackTrace();
            updatedExamData = "Failed to update the exam data";
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send(updatedExamData);
        }
    }
}
