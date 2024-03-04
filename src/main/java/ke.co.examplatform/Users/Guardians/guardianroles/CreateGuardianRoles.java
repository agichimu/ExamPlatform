package ke.co.examplatform.Users.Guardians.guardianroles;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.QuerryManager.QueryManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class CreateGuardianRoles implements HttpHandler {

    private final QueryManager queryManager;

    public CreateGuardianRoles(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(this::handle, this::error);
    }

    public void handle(HttpServerExchange exchange, String message) {
        var roleData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(message, LinkedHashMap.class);

        try {
            String insertQuery = "INSERT INTO guardian_roles " +
                    "(guardian_role) " +
                    "VALUES (?)";

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("guardian_role"));

            int rowsAffected = queryManager.insert(insertQuery, values);

            if (rowsAffected > 0) {
                roleData.put("status", "Guardian role created successfully");
                exchange.setStatusCode(201);
            } else {
                roleData.put("error", "Failed to create guardian role");
                exchange.setStatusCode(400);
            }
        } catch (SQLException | ClassNotFoundException e) {
            roleData.put("error", "Failed to create guardian role");
            roleData.put("details", e.getMessage());
            exchange.setStatusCode(400);
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(gson.toJson(roleData));
    }

    private void error(HttpServerExchange exchange, IOException error) {
        exchange.setStatusCode(400);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("Error in request");
    }
}
