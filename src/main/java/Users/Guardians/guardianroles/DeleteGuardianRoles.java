package Users.Guardians.guardianroles;

import QuerryManager.QueryManager;
import Rest.RestUtils;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class DeleteGuardianRoles implements HttpHandler {

    private final QueryManager queryManager;

    public DeleteGuardianRoles(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        Gson gson = new Gson();

        String deleteQuery = null;
        try {
            String roleId = RestUtils.getPathVar(exchange, "roleId");
            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", roleId);

            deleteQuery = "DELETE FROM guardian_roles WHERE role_id = ?";
            int rowsAffected = queryManager.delete(deleteQuery, values);

            exchange.setStatusCode(200);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

            if (rowsAffected > 0) {
                exchange.getResponseSender().send(gson.toJson("Guardian role deleted successfully"));
            } else {
                exchange.getResponseSender().send(gson.toJson("Failed to delete guardian role"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            exchange.setStatusCode(500);
            e.printStackTrace();
            exchange.getResponseSender().send(gson.toJson("Failed to delete guardian role: " + e.getMessage()));
        }
        System.out.println("Delete Query: " + deleteQuery);
    }
}
