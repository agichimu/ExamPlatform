package ke.co.examplatform.examinations.Choices;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import ke.co.examplatform.QuerryManager.QueryManager;
import ke.co.examplatform.Rest.RestUtils;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class UpdateChoices implements HttpHandler {

    private final QueryManager queryManager;

    public UpdateChoices(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String choiceId = RestUtils.getPathVar(exchange, "choiceId");
        if (choiceId != null) {
            handleUpdate(exchange, choiceId);
        } else {
            sendBadRequestResponse(exchange);
        }
    }

    private void handleUpdate(HttpServerExchange exchange, String choiceId) {
        var updateData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        try {
            String updateQuery = "UPDATE choices_details SET choice_label = ?, " +
                    "choice_content = ?, is_right = ? WHERE choice_id = ?";

            LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(RestUtils.getRequestBody(exchange), LinkedHashMap.class);

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("choice_label"));
            values.put("2", requestBodyMap.get("choice_content"));
            values.put("3", requestBodyMap.get("is_right"));
            values.put("4", choiceId);

            int rowsAffected = queryManager.update(updateQuery, values);

            if (rowsAffected > 0) {
                updateData.put("status", "Choice details updated successfully");
                exchange.setStatusCode(200); // OK
            } else {
                updateData.put("error", "Failed to update choice details");
                exchange.setStatusCode(400); // Bad Request
            }
        } catch (SQLException | ClassNotFoundException e) {
            updateData.put("error", "Failed to update choice details");
            updateData.put("details", e.getMessage());
            exchange.setStatusCode(500); // Internal Server Error
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
