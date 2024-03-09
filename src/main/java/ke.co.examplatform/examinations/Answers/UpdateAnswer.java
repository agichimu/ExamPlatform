package ke.co.examplatform.examinations.Answers;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import ke.co.examplatform.QuerryManager.QueryManager;
import ke.co.examplatform.Rest.RestUtils;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class UpdateAnswer implements HttpHandler {

    private final QueryManager queryManager;

    public UpdateAnswer(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String answerId = RestUtils.getPathVar(exchange, "answerId");
        if (answerId != null) {
            handleUpdate(exchange, answerId);
        } else {
            sendBadRequestResponse(exchange);
        }
    }

    private void handleUpdate(HttpServerExchange exchange, String answerId) {
        var updateData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        try {
            String updateQuery = "UPDATE answer " +
                    "SET choice_id = ?, pupil_id = ?, scores = ?, " +
                    "date_modified = CURRENT_TIMESTAMP " +
                    "WHERE answer_id = ?";

            LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(RestUtils.getRequestBody(exchange), LinkedHashMap.class);

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("choice_id"));
            values.put("2", requestBodyMap.get("pupil_id"));
            values.put("3", requestBodyMap.get("scores"));
            values.put("4", answerId);

            int rowsAffected = queryManager.update(updateQuery, values);

            if (rowsAffected > 0) {
                updateData.put("status", "Answer updated successfully");
                exchange.setStatusCode(StatusCodes.OK); // OK
            } else {
                updateData.put("error", "Failed to update answer");
                exchange.setStatusCode(StatusCodes.NOT_FOUND); // Not Found
            }
        } catch (SQLException | ClassNotFoundException e) {
            updateData.put("error", "Failed to update answer");
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