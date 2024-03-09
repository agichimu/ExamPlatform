package ke.co.examplatform.examinations.Choices;

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

public class GetChoice implements HttpHandler {

    private final QueryManager queryManager;

    public GetChoice(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        String choiceId = RestUtils.getPathVar(exchange, "choiceId");

        if (choiceId != null) {
            try {
                Map<String, Object> choiceMap;
                choiceMap = getChoice(choiceId);
                if (choiceMap.isEmpty()) {
                    sendErrorResponse(exchange, 404, "Choice not found", "choice Id: " + choiceId + " not found or is not valid");
                } else {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    exchange.getResponseSender().send(gson.toJson(choiceMap));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                sendErrorResponse(exchange, 500, "Failed to fetch Choice", null);
            }
        } else {
            sendErrorResponse(exchange, 400, "Choice id not provided", null);
        }
    }

    private Map<String, Object> getChoice(String choiceId) throws SQLException {
        Map<String, Object> choiceMap = new HashMap<>();

        String selectQuery = "SELECT * FROM choices_details WHERE choice_id = ?";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("1", choiceId);

        try {
            List<LinkedHashMap<String, Object>> results = queryManager.select(selectQuery, paramMap);
            if (!results.isEmpty()) {
                choiceMap = results.get(0);
            } else {
                System.out.println("No choice found for ID: " + choiceId);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        // If choiceMap is still empty, the choice ID was not found
        if (choiceMap.isEmpty()) {
            throw new SQLException("Choice ID not found");
        }

        return choiceMap;
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