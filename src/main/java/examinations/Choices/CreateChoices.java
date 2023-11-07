package choices;

import QuerryManager.QueryManager;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class CreateChoices implements HttpHandler {

    private final QueryManager queryManager;

    public CreateChoices(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(this::handle, this::error);
    }

    public void handle(HttpServerExchange exchange, String message) {
        var choiceData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(message, LinkedHashMap.class);

        try {
            String insertQuery = "INSERT INTO choices_details " +
                    "(choice_label, choice_content, is_right, question_id) " +
                    "VALUES (?, ?, ?, ?)";

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("choice_label"));
            values.put("2", requestBodyMap.get("choice_content"));
            values.put("3", requestBodyMap.get("is_right"));
            values.put("4", requestBodyMap.get("question_id"));

            int rowsAffected = queryManager.insert(insertQuery, values);

            if (rowsAffected > 0) {
                choiceData.put("status", "Choice created successfully");
                exchange.setStatusCode(201);
            } else {
                choiceData.put("error", "Failed to create choice");
                exchange.setStatusCode(400);
            }
        } catch (SQLException | ClassNotFoundException e) {
            choiceData.put("error", "Failed to create choice");
            choiceData.put("details", e.getMessage());
            exchange.setStatusCode(400);
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(gson.toJson(choiceData));
    }

    private void error(HttpServerExchange exchange, IOException error) {
        exchange.setStatusCode(400);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("Error in request");
    }
}
