package ke.co.examplatform.Users.Teachers;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import ke.co.examplatform.QuerryManager.QueryManager;
import ke.co.examplatform.Rest.RestUtils;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class UpdateTeachers implements HttpHandler {

    private final QueryManager queryManager;

    public UpdateTeachers(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String teacherId = RestUtils.getPathVar(exchange, "teacherId");
        if (teacherId != null) {
            handleUpdate(exchange, teacherId);
        } else {
            sendBadRequestResponse(exchange);
        }
    }

    private void handleUpdate(HttpServerExchange exchange, String teacherId) {
        var updateData = new LinkedHashMap<String, Object>();
        Gson gson = new Gson();

        try {
            String updateQuery = "UPDATE teachers_details SET first_name = ?, second_name = ?, " +
                    "surname = ?, gender = ?, phone_number = ?, email_address = ?, tsc_number = ?, " +
                    "role = ?, date_of_birth = ?, department_id = ? WHERE teacher_id = ?";

            LinkedHashMap<String, Object> requestBodyMap = gson.fromJson(RestUtils.getRequestBody(exchange), LinkedHashMap.class);

            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", requestBodyMap.get("first_name"));
            values.put("2", requestBodyMap.get("second_name"));
            values.put("3", requestBodyMap.get("surname"));
            values.put("4", requestBodyMap.get("gender"));
            values.put("5", requestBodyMap.get("phone_number"));
            values.put("6", requestBodyMap.get("email_address"));
            values.put("7", requestBodyMap.get("tsc_number"));
            values.put("8", requestBodyMap.get("role"));
            values.put("9", requestBodyMap.get("date_of_birth"));
            values.put("10", requestBodyMap.get("department_id"));
            values.put("11", teacherId);

            int rowsAffected = queryManager.update(updateQuery, values);

            if (rowsAffected > 0) {
                updateData.put("status", "Teacher details updated successfully");
                exchange.setStatusCode(200); //OK
            } else {
                updateData.put("error", "Failed to update teacher details");
                exchange.setStatusCode(404); // Not Found
            }
        } catch (SQLException | ClassNotFoundException e) {
            updateData.put("error", "Failed to update teacher details");
            updateData.put("details", e.getMessage());
            exchange.setStatusCode(500); // Internal Server Error
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(gson.toJson(updateData));
    }

    private void sendBadRequestResponse(HttpServerExchange exchange) {
        exchange.setStatusCode(StatusCodes.BAD_REQUEST);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send("Error in request");
    }
}
