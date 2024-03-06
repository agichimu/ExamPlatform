package ke.co.examplatform.examinations;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.QuerryManager.QueryManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class CreateExaminations implements HttpHandler {

    private final QueryManager queryManager;

    public CreateExaminations(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(this::handle, this::error);
    }

    public void handle(HttpServerExchange exchange, String message) {
        var examData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(message, LinkedHashMap.class);

        try {
            String insertQuery = "INSERT INTO examination_details " +
                    "(instructions, teacher_id, examination_name, subject_id,examination_time) " +
                    "VALUES (?, ?, ?, ?, ?)";


            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("instructions"));
            values.put("2", requestBodyMap.get("teacher_id"));
            values.put("3", requestBodyMap.get("examination_name"));
            values.put("4", requestBodyMap.get("subject_id"));
            values.put("5", requestBodyMap.get("examination_time"));

            int rowsAffected = queryManager.insert(insertQuery, values);

            if (rowsAffected > 0) {
                examData.put("status", "Exam created successfully");
                exchange.setStatusCode(201);
            } else {
                examData.put("error", "Failed to create exams");
                exchange.setStatusCode(400);
            }
        } catch (SQLException | ClassNotFoundException e) {
            examData.put("error", "Failed to create exams");
            examData.put("details", e.getMessage());
            exchange.setStatusCode(400);
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(gson.toJson(examData));
    }

    private void error(HttpServerExchange exchange, IOException error) {
        exchange.setStatusCode(400);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("Error in request");
    }
}
