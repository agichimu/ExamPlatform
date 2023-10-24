package examinations;

import Utilities.ConnectionsXmlReader;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReadExaminations {
    public static void main(String[] args) {
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "localhost")
                .setHandler(exchange -> {
                    if (exchange.getRequestMethod().equalToString("GET") && exchange.getRequestPath().equals("/exams")) {
                        readExams(exchange);
                    } else {
                        exchange.setStatusCode(404);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                        exchange.getResponseSender().send("Not Found");
                    }
                })
                .build();

        server.start();
    }

    public static void readExams(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(() -> readExams(exchange));
            return;
        }

        String examData = "Exam data not available";

        try {
           /*creating db connection*/
            String databaseURL = ConnectionsXmlReader.getDatabaseURL();
            String dbName = ConnectionsXmlReader.getDatabaseName();
            String username = ConnectionsXmlReader.getUsername();
            String password = ConnectionsXmlReader.getPassword();

            String jdbcUrl = databaseURL + dbName;

            try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
                String selectQuery = "SELECT * FROM examination_details";

                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        StringBuilder dataBuilder = new StringBuilder();
                        while (resultSet.next()) {
                            long examinationId = resultSet.getLong("examination_id");
                            String instructions = resultSet.getString("instructions");
                            int teacherId = resultSet.getInt("teacher_id");
                            String examinationName = resultSet.getString("examination_name");
                            int subjectId = resultSet.getInt("subject_id");
                            String examinationTime = resultSet.getString("examination_time");
                            long questionId = resultSet.getLong("question_id");
                            String dateCreated = resultSet.getString("date_created");
                            String dateModified = resultSet.getString("date_modified");

                            dataBuilder.append("Examination ID: ").append(examinationId).append(", Instructions: ").append(instructions).append("\n");
                            dataBuilder.append("Teacher ID: ").append(teacherId).append(", Examination Name: ").append(examinationName).append("\n");
                            dataBuilder.append("Subject ID: ").append(subjectId).append(", Examination Time: ").append(examinationTime).append("\n");
                            dataBuilder.append("Question ID: ").append(questionId).append(", Date Created: ").append(dateCreated).append("\n");
                            dataBuilder.append("Date Modified: ").append(dateModified).append("\n\n");
                        }
                        examData = dataBuilder.toString();
                    }
                }
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(examData);
        } catch (SQLException e) {
            e.printStackTrace();
            examData = "Failed to fetch exam data from the database";
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(examData);
        }
    }
}
