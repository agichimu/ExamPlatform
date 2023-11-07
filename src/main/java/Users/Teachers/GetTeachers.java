package Users.Teachers;

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

public class GetTeachers implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        List<Map<String, Object>> teacherList = new ArrayList<>();

        try {
            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM teachers_details";

                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            Map<String, Object> teacherMap = new HashMap<>();
                            teacherMap.put("teacher_id", resultSet.getLong("teacher_id"));
                            teacherMap.put("first_name", resultSet.getString("first_name"));
                            teacherMap.put("second_name", resultSet.getString("second_name"));
                            teacherMap.put("surname", resultSet.getString("surname"));
                            teacherMap.put("gender", resultSet.getString("gender"));
                            teacherMap.put("phone_number", resultSet.getString("phone_number"));
                            teacherMap.put("email_address", resultSet.getString("email_address"));
                            teacherMap.put("tsc_number", resultSet.getInt("tsc_number"));
                            teacherMap.put("role", resultSet.getString("role"));
                            teacherMap.put("date_of_birth", resultSet.getString("date_of_birth"));
                            teacherMap.put("department_id", resultSet.getLong("department_id"));
                            teacherMap.put("date_created", resultSet.getString("date_created"));
                            teacherMap.put("date_modified", resultSet.getString("date_modified"));

                            teacherList.add(teacherMap);
                        }
                    }
                }
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(teacherList));
        } catch (SQLException e) {
            e.printStackTrace();
            String errorResponse = "Failed to fetch teacher data from the database";
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }
}
