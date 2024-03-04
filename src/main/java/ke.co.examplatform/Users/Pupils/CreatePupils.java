package ke.co.examplatform.Users.Pupils;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.QuerryManager.QueryManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class CreatePupils implements HttpHandler {

    private final QueryManager queryManager;

    public CreatePupils(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(this::handle, this::error);
    }

    public void handle(HttpServerExchange exchange, String message) {
        var pupilData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(message, LinkedHashMap.class);

        try {
            String insertQuery = "INSERT INTO pupils_details " +
                    "(first_name, second_name, surname, gender, admission_no, class_id, admission_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("first_name"));
            values.put("2", requestBodyMap.get("second_name"));
            values.put("3", requestBodyMap.get("surname"));
            values.put("4", requestBodyMap.get("gender"));
            values.put("5", requestBodyMap.get("admission_no"));
            values.put("6", requestBodyMap.get("class_id"));
            values.put("7", requestBodyMap.get("admission_date"));

            int rowsAffected = queryManager.insert(insertQuery, values);

            if (rowsAffected > 0) {
                pupilData.put("status", "Pupil created successfully");
                exchange.setStatusCode(201);
            } else {
                pupilData.put("error", "Failed to create pupil");
                exchange.setStatusCode(400);
            }
        } catch (SQLException | ClassNotFoundException e) {
            pupilData.put("error", "Failed to create pupil");
            pupilData.put("details", e.getMessage());
            exchange.setStatusCode(400);
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(gson.toJson(pupilData));
    }

    private void error(HttpServerExchange exchange, IOException error) {
        exchange.setStatusCode(400);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("Error in request");
    }
}
