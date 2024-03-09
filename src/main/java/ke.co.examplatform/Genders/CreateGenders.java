package ke.co.examplatform.Genders;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.QuerryManager.QueryManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class CreateGenders implements HttpHandler {

    private final QueryManager queryManager;

    public CreateGenders(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(this::handle, this::error);
    }

    public void handle(HttpServerExchange exchange, String message) {
        var genderData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(message, LinkedHashMap.class);

        try {
            String insertQuery = "INSERT INTO genders " +
                    "(gender_name, date_created, date_modified) " +
                    "VALUES (?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("gender_name"));

            int rowsAffected = queryManager.insert(insertQuery, values);

            if (rowsAffected > 0) {
                genderData.put("status", "Gender created successfully");
                exchange.setStatusCode(200);
            } else {
                genderData.put("error", "Failed to create gender");
                exchange.setStatusCode(500);
            }
        } catch (SQLException | ClassNotFoundException e) {
            genderData.put("error", "Failed to create gender");
            genderData.put("details", e.getMessage());
            exchange.setStatusCode(500);
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(gson.toJson(genderData));
    }

    private void error(HttpServerExchange exchange, IOException error) {
        exchange.setStatusCode(500);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("Error in request");
    }
}
