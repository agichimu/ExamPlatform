package ke.co.examplatform.Genders;

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

public class GetGenders implements HttpHandler {

    private static final int DEFAULT_PAGE_SIZE = 10; // Default page size if not specified

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> genderList = new ArrayList<>();

        try {
            // Extracting page and pageSize from query parameters
            Map<String, Deque<String>> queryParams = exchange.getQueryParameters();
            int page = Integer.parseInt(queryParams.getOrDefault("page", new ArrayDeque<>(List.of("1"))).getFirst());
            int pageSize = Integer.parseInt(queryParams.getOrDefault("pageSize", new ArrayDeque<>(List.of(String.valueOf(DEFAULT_PAGE_SIZE)))).getFirst());

            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT COUNT(*) FROM genders";
                int totalRecords;
                try (PreparedStatement countStatement = connection.prepareStatement(selectQuery)) {
                    try (ResultSet countResult = countStatement.executeQuery()) {
                        countResult.next();
                        totalRecords = countResult.getInt(1);
                    }
                }

                int totalPages = (int) Math.ceil((double) totalRecords / pageSize);
                int offset = (page - 1) * pageSize;
                selectQuery = "SELECT * FROM genders LIMIT ? OFFSET ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    preparedStatement.setInt(1, pageSize);
                    preparedStatement.setInt(2, offset);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            Map<String, Object> genderMap = new HashMap<>();
                            genderMap.put("gender_id", resultSet.getLong("gender_id"));
                            genderMap.put("gender_name", resultSet.getString("gender_name"));
                            genderMap.put("date_created", resultSet.getString("date_created"));
                            genderMap.put("date_modified", resultSet.getString("date_modified"));

                            genderList.add(genderMap);
                        }
                    }
                }

                // Create pagination metadata
                Map<String, Object> pagination = new HashMap<>();
                pagination.put("totalRecords", totalRecords);
                pagination.put("lastPage", totalPages); // Assuming lastPage is equivalent to totalPages
                pagination.put("totalPages", totalPages);
                pagination.put("pageSize", pageSize);
                pagination.put("currentPage", page);

                response.put("pagination", pagination);
                response.put("data", genderList);

                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(gson.toJson(response));
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            String errorResponse = "Failed to fetch gender data from the database";
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }
}
