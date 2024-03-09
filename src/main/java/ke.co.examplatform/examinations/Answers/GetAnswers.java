package ke.co.examplatform.examinations.Answers;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.Utilities.ConnectionsXmlReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetAnswers implements HttpHandler {
    private static final int DEFAULT_PAGE_SIZE = 10; // Default page size if not specified

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> answerList = new ArrayList<>();

        try {
            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM answer_detail";

                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            Map<String, Object> answerMap = new HashMap<>();
                            answerMap.put("answer_id", resultSet.getLong("answer_id"));
                            answerMap.put("choice_id", resultSet.getLong("choice_id"));
                            answerMap.put("pupil_id", resultSet.getLong("pupil_id"));
                            answerMap.put("scores", resultSet.getBigDecimal("scores"));
                            answerMap.put("date_created", resultSet.getString("date_created"));
                            answerMap.put("date_modified", resultSet.getString("date_modified"));
                            answerList.add(answerMap);
                        }
                    }
                }
            }

            // Pagination metadata
            int totalRecords = answerList.size();
            int totalPages = (int) Math.ceil((double) totalRecords / DEFAULT_PAGE_SIZE);
            int currentPage = 1; // Since we're not implementing pagination logic here, set to 1 for now

            Map<String, Object> pagination = new HashMap<>();
            pagination.put("totalRecords", totalRecords);
            pagination.put("lastPage", totalPages); // Assuming lastPage is equivalent to totalPages
            pagination.put("totalPages", totalPages);
            pagination.put("pageSize", DEFAULT_PAGE_SIZE);
            pagination.put("currentPage", currentPage);

            response.put("pagination", pagination);
            response.put("data", answerList);

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        } catch (SQLException e) {
            e.printStackTrace();
            String errorResponse = "Failed to fetch answer data from the database";
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }
}
