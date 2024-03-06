package ke.co.examplatform.SchoolManagement.Subjects;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.QuerryManager.QueryManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class CreateSubjects implements HttpHandler {

    private final QueryManager queryManager;

    public CreateSubjects(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(this::handle, this::error);
    }

    public void handle(HttpServerExchange exchange, String message) {
        var subjectData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(message, LinkedHashMap.class);

        try {
            String insertQuery = "INSERT INTO subject_details " +
                    "(subject_name,date_created, date_modified) " +
                    "VALUES (?,CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("subject_name"));
            int rowsAffected = queryManager.insert(insertQuery, values);

            if (rowsAffected > 0) {
                subjectData.put("status", "Subject created successfully");
                exchange.setStatusCode(201);
            } else {
                subjectData.put("error", "Failed to create subject");
                exchange.setStatusCode(400);
            }
        } catch (SQLException | ClassNotFoundException e) {
            subjectData.put("error", "Failed to create subject");
            subjectData.put("details", e.getMessage());
            exchange.setStatusCode(400);
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(gson.toJson(subjectData));
    }

    private void error(HttpServerExchange exchange, IOException error) {
        exchange.setStatusCode(400);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("Error in request");
    }
}
