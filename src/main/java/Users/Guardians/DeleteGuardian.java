package Users.Guardians;

import QuerryManager.QueryManager;
import Rest.RestUtils;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class DeleteGuardian implements HttpHandler {

    private final QueryManager queryManager;

    public DeleteGuardian(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        Gson gson = new Gson();

        String deleteQuery = null;
        try {
            String guardianId = RestUtils.getPathVar(exchange, "guardianId");
            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", guardianId);

            deleteQuery = "DELETE FROM guardian_details WHERE guardian_id = ?";
            int rowsAffected = queryManager.delete(deleteQuery, values);

            exchange.setStatusCode(200);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

            if (rowsAffected > 0) {
                exchange.getResponseSender().send(gson.toJson("Guardian deleted successfully"));
            } else {
                exchange.getResponseSender().send(gson.toJson("Failed to delete guardian"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            exchange.setStatusCode(500);
            e.printStackTrace();
            exchange.getResponseSender().send(gson.toJson("Failed to delete guardian: " + e.getMessage()));
        }
        System.out.println("Delete Query: " + deleteQuery);
    }
}
