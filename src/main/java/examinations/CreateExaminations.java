package examinations;

import Utilities.ConnectionsXmlReader;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.sql.*;


public class CreateExaminations {

    public static void main(String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(exchange -> {
                    if (exchange.getRequestMethod().equalToString("POST") && exchange.getRequestPath().equals("/exams")) {
                        createExam(exchange);
                    } else {
                        exchange.setStatusCode(404);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                        exchange.getResponseSender().send("Not Found");
                    }
                })
                .build();

        server.start();
    }

    public static void createExam(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(() -> createExam(exchange));
            return;
        }

        exchange.getRequestReceiver().receiveFullString((exchange1, message) -> {
            String examData = "Exam created successfully";

            try {
                /*Establishing db connection*/
                String databaseURL = ConnectionsXmlReader.getDatabaseURL();
                String dbName = ConnectionsXmlReader.getDatabaseName();
                String username = ConnectionsXmlReader.getUsername();
                String password = ConnectionsXmlReader.getPassword();

                String jdbcUrl = databaseURL + dbName;

                try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
                  /*Insertion Query*/
                    String insertQuery = "INSERT INTO examination_details (instructions, teacher_id, " +
                            "examination_name, subject_id, question_id, examination_time, date_created, " +
                            "date_modified) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                    /*Prepared statements*/
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                        preparedStatement.setString(1, "Answer only one question");
                        preparedStatement.setInt(2, 1);
                        preparedStatement.setString(3, "Computer studies");
                        preparedStatement.setInt(4, 5);
                        preparedStatement.setInt(5, 1);
                        preparedStatement.setTimestamp(6, Timestamp.valueOf("2023-10-24 03:05:00"));
                        java.util.Date today = new java.util.Date();
                        preparedStatement.setTimestamp(7, new Timestamp(today.getTime()));
                        preparedStatement.setTimestamp(8, new Timestamp(today.getTime()));

                        /* Executing insertion statement */
                        int rowsAffected = preparedStatement.executeUpdate();

                        if (rowsAffected > 0) {
                            examData = "Exam created successfully";
                        } else {
                            examData = "Failed to create the exam";
                        }
                    }
                }

                exchange1.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange1.getResponseSender().send(examData);
            } catch (SQLException e) {
                e.printStackTrace();
                examData = "Failed to create the exam";
                exchange1.setStatusCode(500); 
                exchange1.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange1.getResponseSender().send(examData);
            }
        }, (exchange1, error) -> {
            exchange1.setStatusCode(400);
            exchange1.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange1.getResponseSender().send("Request Not Successful");
        });
    }
}
