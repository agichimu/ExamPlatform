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
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(gson.toJson(choiceMap));
            } catch (SQLException e) {
                e.printStackTrace();
                String errorResponse = "Failed to fetch choice";
                exchange.setStatusCode(500);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(errorResponse);
            }
        } else {
            String errorResponse = "Choice ID not provided";
            exchange.setStatusCode(400);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }

    private Map<String, Object> getChoice(String choiceId) throws SQLException {
        Map<String, Object> choiceMap = new HashMap<>();

        String selectQuery = "SELECT * FROM choices_details WHERE choice_id = ?";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("choiceId", choiceId);

        try {
            List<LinkedHashMap<String, Object>> results = queryManager.select(selectQuery, paramMap);
            if (!results.isEmpty()) {
                choiceMap = results.get(0);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return choiceMap;
    }
}
