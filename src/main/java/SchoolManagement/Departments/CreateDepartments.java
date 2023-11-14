package SchoolManagement.Departments;

import QuerryManager.QueryManager;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class CreateDepartments implements HttpHandler {

    private final QueryManager queryManager;

    public CreateDepartments(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(this::handle, this::error);
    }

    public void handle(HttpServerExchange exchange, String message) {
        var departmentData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(message, LinkedHashMap.class);

        try {
            String insertQuery = "INSERT INTO department_details " +
                    "(department_name, date_created, date_modified) " +
                    "VALUES (?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("department_name"));

            int rowsAffected = queryManager.insert(insertQuery, values);

            if (rowsAffected > 0) {
                departmentData.put("status", "Department created successfully");
                exchange.setStatusCode(201);
            } else {
                departmentData.put("error", "Failed to create department");
                exchange.setStatusCode(400);
            }
        } catch (SQLException | ClassNotFoundException e) {
            departmentData.put("error", "Failed to create department");
            departmentData.put("details", e.getMessage());
            exchange.setStatusCode(400);
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(gson.toJson(departmentData));
    }

    private void error(HttpServerExchange exchange, IOException error) {
        exchange.setStatusCode(400);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("Error in request");
    }
}
