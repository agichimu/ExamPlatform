package SchoolManagement.Classes; // Adjust the package name as per your project structure

import QuerryManager.QueryManager;
import Rest.RestUtils;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class DeleteClasses implements HttpHandler {

    private final QueryManager queryManager;

    public DeleteClasses(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        Gson gson = new Gson();

        String deleteQuery = null;
        try {
            String strClassId = RestUtils.getPathVar(exchange, "classId");
            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", strClassId);

            deleteQuery = "DELETE FROM class_details WHERE class_id = ?";
            int rowsAffected = queryManager.delete(deleteQuery, values);

            exchange.setStatusCode(200);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

            if (rowsAffected > 0) {
                exchange.getResponseSender().send(gson.toJson("Class deleted successfully"));
            } else {
                exchange.getResponseSender().send(gson.toJson("Failed to delete class"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            exchange.setStatusCode(500);
            e.printStackTrace();
            exchange.getResponseSender().send(gson.toJson("Failed to delete class: " + e.getMessage()));
        }
        System.out.println("Delete Query: " + deleteQuery);
    }
}
