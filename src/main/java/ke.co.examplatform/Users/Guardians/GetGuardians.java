package ke.co.examplatform.Users.Guardians;

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

public class GetGuardians implements HttpHandler {

    private static final int DEFAULT_PAGE_SIZE = 10; // Default page size if not specified

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> guardianList = new ArrayList<>();

        try {
            // Extracting page and pageSize from query parameters
            Map<String, Deque<String>> queryParams = exchange.getQueryParameters();
            int page = Integer.parseInt(queryParams.getOrDefault("page", new ArrayDeque<>(List.of("1"))).getFirst());
            int pageSize = Integer.parseInt(queryParams.getOrDefault("pageSize", new ArrayDeque<>(List.of(String.valueOf(DEFAULT_PAGE_SIZE)))).getFirst());

            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String countQuery = "SELECT COUNT(*) FROM guardian_details";
                int totalRecords;
                try (PreparedStatement countStatement = connection.prepareStatement(countQuery);
                     ResultSet countResult = countStatement.executeQuery()) {
                    countResult.next();
                    totalRecords = countResult.getInt(1);
                }

                int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
                int offset = (page - 1) * pageSize;

                String selectQuery = "SELECT * FROM guardian_details LIMIT ? OFFSET ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    preparedStatement.setInt(1, pageSize);
                    preparedStatement.setInt(2, offset);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            Map<String, Object> guardianMap = new HashMap<>();
                            guardianMap.put("guardian_id", resultSet.getLong("guardian_id"));
                            guardianMap.put("first_name", resultSet.getString("first_name"));
                            guardianMap.put("surname", resultSet.getString("surname"));
                            guardianMap.put("phone_number", resultSet.getString("phone_number"));
                            guardianMap.put("gender_id", resultSet.getInt("gender_id"));
                            guardianMap.put("date_created", resultSet.getString("date_created"));
                            guardianMap.put("date_modified", resultSet.getString("date_modified"));
                            guardianList.add(guardianMap);
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

            response.put("data", guardianList);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            String errorResponse = "Failed to fetch guardian data from the database";
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }
}
