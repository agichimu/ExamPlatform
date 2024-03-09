package ke.co.examplatform.examinations.Questions;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.QuerryManager.QueryManager;
import ke.co.examplatform.Rest.RestUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GetQuestion implements HttpHandler {

    private final QueryManager queryManager;

    public GetQuestion(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        String questionId = RestUtils.getPathVar(exchange, "questionId");

        if (questionId != null) {
            try {
                Map<String, Object> questionMap;
                questionMap = getQuestion(questionId);
                if (questionMap.isEmpty()) {
                    sendErrorResponse(exchange, 404, "Question not found", "Question ID: " + questionId + " not found or is not valid");
                } else {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    exchange.getResponseSender().send(gson.toJson(questionMap));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, "Failed to fetch question", null);
            }
        } else {
            sendErrorResponse(exchange, 400, "Question ID not provided", null);
        }
    }

    private Map<String, Object> getQuestion(String questionId) throws SQLException {
        Map<String, Object> questionMap = new HashMap<>();

        String selectQuery = "SELECT * FROM questions_details WHERE question_id = ?";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("1", questionId);

        try {
            List<LinkedHashMap<String, Object>> results = queryManager.select(selectQuery, paramMap);
            if (!results.isEmpty()) {
                questionMap = results.get(0);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // If questionMap is still empty, the question ID was not found
        if (questionMap.isEmpty()) {
            throw new SQLException("Question ID not found");
        }

        return questionMap;
    }


    private void sendErrorResponse(HttpServerExchange exchange, int statusCode, String message, String details) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        Gson gson = new Gson();
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        if (details != null) {
            errorResponse.put("details", details);
        }
        exchange.getResponseSender().send(gson.toJson(errorResponse));
    }
}