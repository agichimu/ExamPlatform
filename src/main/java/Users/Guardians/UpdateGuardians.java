package Users.Guardians;

import QuerryManager.QueryManager;
import Rest.RestUtils;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class UpdateGuardians implements HttpHandler {

    private final QueryManager queryManager;

    public UpdateGuardians(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String guardianId = RestUtils.getPathVar(exchange, "guardianId");
        if (guardianId != null) {
            handleUpdate(exchange, guardianId);
        } else {
            sendBadRequestResponse(exchange);
        }
    }

    private void handleUpdate(HttpServerExchange exchange, String guardianId) {
        var updateData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        try {
            String updateQuery = "UPDATE guardian_details SET first_name = ?, second_name = ?, " +
                    "surname = ?, phone_number = ?, email_address = ?, gender = ? " +
                    "WHERE guardian_id = ?";

            LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(RestUtils.getRequestBody(exchange), LinkedHashMap.class);

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("first_name"));
            values.put("2", requestBodyMap.get("second_name"));
            values.put("3", requestBodyMap.get("surname"));
            values.put("4", requestBodyMap.get("phone_number"));
            values.put("5", requestBodyMap.get("email_address"));
            values.put("6", requestBodyMap.get("gender"));
            values.put("7", guardianId);

            int rowsAffected = queryManager.update(updateQuery, values);

            if (rowsAffected > 0) {
                updateData.put("status", "Guardian details updated successfully");
                exchange.setStatusCode(200); // HTTP 200 - OK
            } else {
                updateData.put("error", "Failed to update guardian details");
                exchange.setStatusCode(400); // HTTP 404 - Not Found
            }
        } catch (SQLException | ClassNotFoundException e) {
            updateData.put("error", "Failed to update guardian details");
            updateData.put("details", e.getMessage());
            exchange.setStatusCode(500); // Internal Server Error
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(gson.toJson(updateData));
    }

    private void sendBadRequestResponse(HttpServerExchange exchange) {
        exchange.setStatusCode(400);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("Error in request");
    }
}
