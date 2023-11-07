package examinations;

import QuerryManager.QueryManager;
import Rest.RestUtils;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class UpdateExaminations implements HttpHandler {

    private final QueryManager queryManager;

    public UpdateExaminations(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String examinationId = RestUtils.getPathVar(exchange, "examinationId");
        if (examinationId != null) {
            handleUpdate(exchange, examinationId);
        } else {
            sendBadRequestResponse(exchange);
        }
    }

    private void handleUpdate(HttpServerExchange exchange, String examinationId) {
        var updateData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        try {
            String updateQuery = "UPDATE examination_details SET instructions = ?, teacher_id = ?, examination_name = ? " +
                    "WHERE examination_id = ?";

            LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(RestUtils.getRequestBody(exchange), LinkedHashMap.class);

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("instructions"));
            values.put("2", requestBodyMap.get("teacher_id"));
            values.put("3", requestBodyMap.get("examination_name"));
            values.put("4", examinationId);

            int rowsAffected = queryManager.update(updateQuery, values);

            if (rowsAffected > 0) {
                updateData.put("status", "Examination details updated successfully");
                exchange.setStatusCode(200); // OK
            } else {
                updateData.put("error", "Failed to update examination details");
                exchange.setStatusCode(400); // bad request
            }
        } catch (SQLException | ClassNotFoundException e) {
            updateData.put("error", "Failed to update examination details");
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
