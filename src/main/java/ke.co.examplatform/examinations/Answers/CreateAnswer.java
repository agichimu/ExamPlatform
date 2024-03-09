package ke.co.examplatform.examinations.Answers;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.QuerryManager.QueryManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class CreateAnswer implements HttpHandler {

    private final QueryManager queryManager;

    public CreateAnswer(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(this::handle, this::error);
    }

    public void handle(HttpServerExchange exchange, String message) {
        var answerData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(message, LinkedHashMap.class);

        try {
            String insertQuery = "INSERT INTO answer " +
                    "(choice_id, pupil_id, scores, date_created, date_modified) " +
                    "VALUES (?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("choice_id"));
            values.put("2", requestBodyMap.get("pupil_id"));
            values.put("3", requestBodyMap.get("scores"));

            int rowsAffected = queryManager.insert(insertQuery, values);

            if (rowsAffected > 0) {
                answerData.put("status", "Answer created successfully");
                exchange.setStatusCode(200);
            } else {
                answerData.put("error", "Failed to create answer");
                exchange.setStatusCode(500);
            }
        } catch (SQLException | ClassNotFoundException e) {
            answerData.put("error", "Failed to create answer");
            answerData.put("details", e.getMessage());
            exchange.setStatusCode(500);
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(gson.toJson(answerData));
    }

    private void error(HttpServerExchange exchange, IOException error) {
        exchange.setStatusCode(500);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("Error in request");
    }
}
