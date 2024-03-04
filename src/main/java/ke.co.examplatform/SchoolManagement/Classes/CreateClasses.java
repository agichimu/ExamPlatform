package ke.co.examplatform.SchoolManagement.Classes;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.QuerryManager.QueryManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class CreateClasses implements HttpHandler {

    private final QueryManager queryManager;

    public CreateClasses(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(this::handle, this::error);
    }

    public void handle(HttpServerExchange exchange, String message) {
        var classData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(message, LinkedHashMap.class);

        try {
            String insertQuery = "INSERT INTO class_details " + "(class_name) " + "VALUES (?)";

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("class_name"));

            int rowsAffected = queryManager.insert(insertQuery, values);

            if (rowsAffected > 0) {
                classData.put("status", "Class created successfully");
                exchange.setStatusCode(201);
            } else {
                classData.put("error", "Failed to create class");
                exchange.setStatusCode(400);
            }
        } catch (SQLException | ClassNotFoundException e) {
            classData.put("error", "Failed to create class");
            classData.put("details", e.getMessage());
            exchange.setStatusCode(500); // internal server error
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(gson.toJson(classData));
    }

    private void error(HttpServerExchange exchange, IOException error) {
        exchange.setStatusCode(500);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("Error processing request: " + error.getMessage());
    }
}
