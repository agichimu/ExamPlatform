package ke.co.examplatform.examinations.Choices;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.QuerryManager.QueryManager;
import ke.co.examplatform.Rest.RestUtils;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class DeleteChoices implements HttpHandler {

    private final QueryManager queryManager;

    public DeleteChoices(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        Gson gson = new Gson();

        String deleteQuery = null;
        try {
            String strChoiceId = RestUtils.getPathVar(exchange, "choiceId");
            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", strChoiceId);

            deleteQuery = "DELETE FROM choices_details WHERE choice_id = ?";
            int rowsAffected = queryManager.delete(deleteQuery, values);

            exchange.setStatusCode(200);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

            if (rowsAffected > 0) {
                exchange.getResponseSender().send(gson.toJson("Choice deleted successfully"));
            } else {
                exchange.getResponseSender().send(gson.toJson("Failed to delete choice"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            exchange.setStatusCode(500);
            e.printStackTrace();
            exchange.getResponseSender().send(gson.toJson("Failed to delete choice: " + e.getMessage()));
        }
        System.out.println("Delete Query: " + deleteQuery);
    }
}
