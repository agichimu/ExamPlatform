package ke.co.examplatform.Genders;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.QuerryManager.QueryManager;
import ke.co.examplatform.Rest.RestUtils;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class DeleteGenders implements HttpHandler {

    private final QueryManager queryManager;

    public DeleteGenders(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        Gson gson = new Gson();

        String deleteQuery = null;
        try {
            String strGenderId = RestUtils.getPathVar(exchange, "genderId");
            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", strGenderId);

            deleteQuery = "DELETE FROM genders WHERE gender_id = ?";
            int rowsAffected = queryManager.delete(deleteQuery, values);

            exchange.setStatusCode(200);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

            if (rowsAffected > 0) {
                exchange.getResponseSender().send(gson.toJson("Gender deleted successfully"));
            } else {
                exchange.getResponseSender().send(gson.toJson("Failed to delete gender"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            exchange.setStatusCode(500);
            e.printStackTrace();
            exchange.getResponseSender().send(gson.toJson("Failed to delete gender: " + e.getMessage()));
        }
        System.out.println("Delete Query: " + deleteQuery);
    }
}