package SchoolManagement.Departments;

import Utilities.ConnectionsXmlReader;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetDepartments implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        List<Map<String, Object>> departmentList = new ArrayList<>();

        String selectQuery = "SELECT * FROM department_details";

        try (
                Connection connection = ConnectionsXmlReader.getDbConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                Map<String, Object> departmentMap = new HashMap<>();
                departmentMap.put("department_id", resultSet.getLong("department_id"));
                departmentMap.put("department_name", resultSet.getString("department_name"));
                departmentMap.put("date_created", resultSet.getTimestamp("date_created"));
                departmentMap.put("date_modified", resultSet.getTimestamp("date_modified"));

                departmentList.add(departmentMap);
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(departmentList));
        } catch (SQLException e) {
            e.printStackTrace();
            String errorResponse = gson.toJson("Failed to fetch department data: " + e.getMessage());
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        } finally {
            System.out.println("request finished");
        }
    }
}
