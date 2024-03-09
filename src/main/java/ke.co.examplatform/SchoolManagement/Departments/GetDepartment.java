package ke.co.examplatform.Users.Departments;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import ke.co.examplatform.Utilities.ConnectionsXmlReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetDepartment implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String departmentIdParam = exchange.getQueryParameters().getOrDefault("departmentId", new ArrayDeque<>(List.of("department_id"))).getFirst();


        if (departmentIdParam.isEmpty()) {
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseSender().send("Department ID not provided");
            return;
        }

        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();

        try {
            long departmentId = Long.parseLong(departmentIdParam);

            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM department_details WHERE department_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    preparedStatement.setLong(1, departmentId);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            Map<String, Object> departmentMap = new HashMap<>();
                            departmentMap.put("department_id", resultSet.getLong("department_id"));
                            departmentMap.put("department_name", resultSet.getString("department_name"));
                            departmentMap.put("date_created", resultSet.getString("date_created"));
                            departmentMap.put("date_modified", resultSet.getString("date_modified"));

                            response.put("data", departmentMap);
                            exchange.setStatusCode(StatusCodes.OK);
                        } else {
                            response.put("error", "Department not found");
                            exchange.setStatusCode(StatusCodes.NOT_FOUND);
                        }
                    }
                }

                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(gson.toJson(response));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.put("error", "Invalid department ID format");
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("error", "Failed to fetch department data from the database");
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        }
    }
}
