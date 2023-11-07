package examinations.Questions;

import QuerryManager.QueryManager;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class CreateQuestions implements HttpHandler {

    private final QueryManager queryManager;

    public CreateQuestions(QueryManager queryManager) {

        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(this::handle, this::error);
    }

    public void handle(HttpServerExchange exchange, String message) {
        var questionData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(message, LinkedHashMap.class);

        try {
            String insertQuery = "INSERT INTO questions_details " +
                    "(question_layout, question_text, question_total_marks, question_time, examination_id, question_type) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("question_layout"));
            values.put("2", requestBodyMap.get("question_text"));
            values.put("3", requestBodyMap.get("question_total_marks"));
            values.put("4", requestBodyMap.get("question_time"));
            values.put("5", requestBodyMap.get("examination_id"));
            values.put("6", requestBodyMap.get("question_type"));

            int rowsAffected = queryManager.insert(insertQuery, values);

            if (rowsAffected > 0) {
                questionData.put("status", "Question created successfully");
                exchange.setStatusCode(201);
            } else {
                questionData.put("error", "Failed to create question");
                exchange.setStatusCode(400);
            }
        } catch (SQLException | ClassNotFoundException e) {
            questionData.put("error", "Failed to create question");
            questionData.put("details", e.getMessage());
            exchange.setStatusCode(400);
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(gson.toJson(questionData));
    }

    private void error(HttpServerExchange exchange, IOException error) {
        exchange.setStatusCode(400);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("Error in request");
    }
}
