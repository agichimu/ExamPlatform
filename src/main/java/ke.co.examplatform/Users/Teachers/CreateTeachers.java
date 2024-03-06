package ke.co.examplatform.Users.Teachers;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.QuerryManager.QueryManager;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedHashMap;

public class CreateTeachers implements HttpHandler {

    private final QueryManager queryManager;

    public CreateTeachers(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        exchange.getRequestReceiver().receiveFullString(this::handle, this::error);
    }

    public void handle(HttpServerExchange exchange, String message) {
        var teacherData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(message, LinkedHashMap.class);

        try {
            String insertQuery = "INSERT INTO teachers_details " +
                    "(first_name, last_name, phone_number, tsc_number, email_id, " +
                    "gender_id, birthdate, hire_date, department_id, " +
                    "years_of_experience, education_level) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("first_name"));
            values.put("2", requestBodyMap.get("last_name"));
            values.put("3", requestBodyMap.get("phone_number"));
            values.put("4", requestBodyMap.get("tsc_number"));
            values.put("5", requestBodyMap.get("email_id"));
            values.put("6", requestBodyMap.get("gender_id"));
            values.put("7", requestBodyMap.get("birthdate"));
            values.put("8", requestBodyMap.get("hire_date"));
            values.put("9", requestBodyMap.get("department_id"));
            values.put("10", requestBodyMap.get("years_of_experience"));
            values.put("11", requestBodyMap.get("education_level"));

            int rowsAffected = queryManager.insert(insertQuery, values);

            if (rowsAffected > 0) {
                teacherData.put("status", "Teacher created successfully");
                exchange.setStatusCode(201);
            } else {
                teacherData.put("error", "Failed to create teacher");
                exchange.setStatusCode(400);
            }
        } catch (SQLException | ClassNotFoundException e) {
            teacherData.put("error", "Failed to create teacher");
            teacherData.put("details", e.getMessage());
            exchange.setStatusCode(400);
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(gson.toJson(teacherData));
    }

    private void error(HttpServerExchange exchange, IOException error) {
        exchange.setStatusCode(400);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("Error in request");
    }
}
