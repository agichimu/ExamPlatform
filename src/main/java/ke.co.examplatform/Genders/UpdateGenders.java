package ke.co.examplatform.Genders;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import ke.co.examplatform.QuerryManager.QueryManager;
import ke.co.examplatform.Rest.RestUtils;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class UpdateGenders implements HttpHandler {

    private final QueryManager queryManager;

    public UpdateGenders(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String genderId = RestUtils.getPathVar(exchange, "genderId");
        if (genderId != null) {
            handleUpdate(exchange, genderId);
        } else {
            sendBadRequestResponse(exchange);
        }
    }

    private void handleUpdate(HttpServerExchange exchange, String genderId) {
        var updateData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        try {
            String updateQuery = "UPDATE genders " +
                    "SET gender_name = ?, date_modified = CURRENT_TIMESTAMP " +
                    "WHERE gender_id = ?";

            LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(RestUtils.getRequestBody(exchange), LinkedHashMap.class);

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("gender_name"));
            values.put("2", genderId);

            int rowsAffected = queryManager.update(updateQuery, values);

            if (rowsAffected > 0) {
                updateData.put("status", "Gender details updated successfully");
                exchange.setStatusCode(StatusCodes.OK); // OK
            } else {
                updateData.put("error", "Failed to update gender details");
                exchange.setStatusCode(StatusCodes.NOT_FOUND); // Not Found
            }
        } catch (SQLException | ClassNotFoundException e) {
            updateData.put("error", "Failed to update gender details");
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
