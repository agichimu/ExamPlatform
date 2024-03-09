package ke.co.examplatform.Users.Teachers;

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

public class GetTeacher implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String teacherIdParam = exchange.getQueryParameters().getOrDefault("teacherId", new ArrayDeque<>(List.of("teacher_id"))).getFirst();

        if (teacherIdParam.isEmpty()) {
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseSender().send("Teacher ID not provided");
            return;
        }
        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();

        try {
            long teacherId = Long.parseLong(teacherIdParam);

            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM teachers_details WHERE teacher_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    preparedStatement.setLong(1, teacherId);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            Map<String, Object> teacherMap = new HashMap<>();
                            teacherMap.put("teacher_id", resultSet.getLong("teacher_id"));
                            teacherMap.put("first_name", resultSet.getString("first_name"));
                            teacherMap.put("last_name", resultSet.getString("last_name"));
                            teacherMap.put("phone_number", resultSet.getString("phone_number"));
                            teacherMap.put("tsc_number", resultSet.getLong("tsc_number"));
                            teacherMap.put("email_id", resultSet.getString("email_id"));
                            teacherMap.put("gender_id", resultSet.getInt("gender_id"));
                            teacherMap.put("birthdate", resultSet.getString("birthdate"));
                            teacherMap.put("hire_date", resultSet.getString("hire_date"));
                            teacherMap.put("department_id", resultSet.getLong("department_id"));
                            teacherMap.put("years_of_experience", resultSet.getInt("years_of_experience"));
                            teacherMap.put("education_level", resultSet.getString("education_level"));
                            teacherMap.put("date_created", resultSet.getString("date_created"));
                            teacherMap.put("date_modified", resultSet.getString("date_modified"));

                            response.put("data", teacherMap);
                            exchange.setStatusCode(StatusCodes.OK);
                        } else {
                            response.put("error", "Teacher not found");
                            exchange.setStatusCode(StatusCodes.NOT_FOUND);
                        }
                    }
                }

                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(gson.toJson(response));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseSender().send("Invalid teacher ID format");
        } catch (SQLException e) {
            e.printStackTrace();
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.getResponseSender().send("Failed to fetch teacher data from the database");
        }
    }
}
