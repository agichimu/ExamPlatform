package Users.Pupils;

import QuerryManager.QueryManager;
import Rest.RestUtils;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class UpdatePupils implements HttpHandler {

    private final QueryManager queryManager;

    public UpdatePupils(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String pupilId = RestUtils.getPathVar(exchange, "pupilId");
        if (pupilId != null) {
            handleUpdate(exchange, pupilId);
        } else {
            sendBadRequestResponse(exchange);
        }
    }

    private void handleUpdate(HttpServerExchange exchange, String pupilId) {
        var updateData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        try {
            String updateQuery = "UPDATE pupils_details " +
                    "SET first_name = ?, second_name = ?, surname = ?, gender = ?, " +
                    "admission_no = ?, class_id = ?, admission_date = ? " +
                    "WHERE pupil_id = ?";

            LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(RestUtils.getRequestBody(exchange), LinkedHashMap.class);

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("first_name"));
            values.put("2", requestBodyMap.get("second_name"));
            values.put("3", requestBodyMap.get("surname"));
            values.put("4", requestBodyMap.get("gender"));
            values.put("5", requestBodyMap.get("admission_no"));
            values.put("6", requestBodyMap.get("class_id"));
            values.put("7", requestBodyMap.get("admission_date"));
            values.put("8", pupilId);

            int rowsAffected = queryManager.update(updateQuery, values);

            if (rowsAffected > 0) {
                updateData.put("status", "Pupil details updated successfully");
                exchange.setStatusCode(StatusCodes.OK); // HTTP 200 - OK
            } else {
                updateData.put("error", "Failed to update pupil details");
                exchange.setStatusCode(StatusCodes.NOT_FOUND); // HTTP 404 - Not Found
            }
        } catch (SQLException | ClassNotFoundException e) {
            updateData.put("error", "Failed to update pupil details");
            updateData.put("details", e.getMessage());
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR); // Internal Server Error
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(gson.toJson(updateData));
    }

    private void sendBadRequestResponse(HttpServerExchange exchange) {
        exchange.setStatusCode(StatusCodes.BAD_REQUEST);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("Error in request");
    }
}
