package ke.co.examplatform.examinations.Questions;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import ke.co.examplatform.QuerryManager.QueryManager;
import ke.co.examplatform.Rest.RestUtils;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class UpdateQuestions implements HttpHandler {

    private final QueryManager queryManager;

    public UpdateQuestions(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String questionId = RestUtils.getPathVar(exchange, "questionId");
        if (questionId != null) {
            handleUpdate(exchange, questionId);
        } else {
            sendBadRequestResponse(exchange);
        }
    }

    private void handleUpdate(HttpServerExchange exchange, String questionId) {
        var updateData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        try {
            String updateQuery = "UPDATE questions_details SET question_layout = ?, question_text = ?, " +
                    "question_total_marks = ?, question_time = ?, examination_id = ?, question_type = ? " +
                    "WHERE question_id = ?";

            LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(RestUtils.getRequestBody(exchange), LinkedHashMap.class);

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("question_layout"));
            values.put("2", requestBodyMap.get("question_text"));
            values.put("3", requestBodyMap.get("question_total_marks"));
            values.put("4", requestBodyMap.get("question_time"));
            values.put("5", requestBodyMap.get("examination_id"));
            values.put("6", requestBodyMap.get("question_type"));
            values.put("7", questionId);

            int rowsAffected = queryManager.update(updateQuery, values);

            if (rowsAffected > 0) {
                updateData.put("status", "Question details updated successfully");
                exchange.setStatusCode(200); //OK
            } else {
                updateData.put("error", "Failed to update question details");
                exchange.setStatusCode(404); //Not Found
            }
        } catch (SQLException | ClassNotFoundException e) {
            updateData.put("error", "Failed to update question details");
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
