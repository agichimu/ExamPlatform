package Users.Guardians;

import QuerryManager.QueryManager;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class CreateGuardian implements HttpHandler {

    private final QueryManager queryManager;


    public CreateGuardian(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(this::handle, this::error);
    }

    public void handle(HttpServerExchange exchange, String message) {
        var guardianData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(message, LinkedHashMap.class);

        try {
            String insertQuery = "INSERT INTO guardian_details " +
                    "(first_name, second_name, surname, gender, phone_number, email_address) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("first_name"));
            values.put("2", requestBodyMap.get("second_name"));
            values.put("3", requestBodyMap.get("surname"));
            values.put("4", requestBodyMap.get("gender"));
            values.put("5", requestBodyMap.get("phone_number"));
            values.put("6", requestBodyMap.get("email_address"));

            int rowsAffected = queryManager.insert(insertQuery, values);

            if (rowsAffected > 0) {
                guardianData.put("status", "Guardian created successfully");
                exchange.setStatusCode(201);
            } else {
                guardianData.put("error", "Failed to create guardian");
                exchange.setStatusCode(400);
            }
        } catch (SQLException | ClassNotFoundException e) {
            guardianData.put("error", "Failed to create guardian");
            guardianData.put("details", e.getMessage());
            exchange.setStatusCode(400);
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(gson.toJson(guardianData));
    }

    private void error(HttpServerExchange exchange, IOException error) {
        exchange.setStatusCode(400);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("Error in request");
    }
}
