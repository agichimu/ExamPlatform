package ke.co.examplatform.Users.Teachers;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.Utilities.ConnectionsXmlReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GetTeachers implements HttpHandler {

    private static final int DEFAULT_PAGE_SIZE = 10; // Default page size if not specified

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> teacherList = new ArrayList<>();

        try {
            // Extracting page and pageSize from query parameters
            Map<String, Deque<String>> queryParams = exchange.getQueryParameters();
            int page = Integer.parseInt(queryParams.getOrDefault("page", new ArrayDeque<>(List.of("1"))).getFirst());
            int pageSize = Integer.parseInt(queryParams.getOrDefault("pageSize", new ArrayDeque<>(List.of(String.valueOf(DEFAULT_PAGE_SIZE)))).getFirst());

            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String countQuery = "SELECT COUNT(*) FROM teachers_details";
                int totalRecords;
                try (PreparedStatement countStatement = connection.prepareStatement(countQuery);
                     ResultSet countResult = countStatement.executeQuery()) {
                    countResult.next();
                    totalRecords = countResult.getInt(1);
                }

                int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
                int offset = (page - 1) * pageSize;

                String selectQuery = "SELECT * FROM teachers_details LIMIT ? OFFSET ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    preparedStatement.setInt(1, pageSize);
                    preparedStatement.setInt(2, offset);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
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

                            teacherList.add(teacherMap);
                        }
                    }
                }

                // Pagination metadata
                Map<String, Object> pagination = new HashMap<>();
                pagination.put("totalRecords", totalRecords);
                pagination.put("totalPages", totalPages);
                pagination.put("pageSize", pageSize);
                pagination.put("currentPage", page);
                response.put("pagination", pagination);
            }

            response.put("data", teacherList);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            String errorResponse = "Failed to fetch teacher data from the database";
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }
}
