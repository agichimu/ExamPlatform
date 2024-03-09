package ke.co.examplatform.examinations.Answers;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.QuerryManager.QueryManager;
import ke.co.examplatform.Rest.RestUtils;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class DeleteAnswer implements HttpHandler {

    private final QueryManager queryManager;

    public DeleteAnswer(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        Gson gson = new Gson();

        String deleteQuery = null;
        try {
            String strAnswerId = RestUtils.getPathVar(exchange, "answerId");
            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", strAnswerId);

            deleteQuery = "DELETE FROM answer WHERE answer_id = ?";
            int rowsAffected = queryManager.delete(deleteQuery, values);

            exchange.setStatusCode(200);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

            if (rowsAffected > 0) {
                exchange.getResponseSender().send(gson.toJson("Answer deleted successfully"));
            } else {
                exchange.getResponseSender().send(gson.toJson("Failed to delete answer"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            exchange.setStatusCode(500);
            e.printStackTrace();
            exchange.getResponseSender().send(gson.toJson("Failed to delete answer: " + e.getMessage()));
        }
        System.out.println("Delete Query: " + deleteQuery);
    }
}