package ke.co.examplatform.Users.Subjects;

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

public class GetSubject implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String subjectIdParam = exchange.getQueryParameters().getOrDefault("subjectId", new ArrayDeque<>(List.of("subject_id"))).getFirst();


        if (subjectIdParam.isEmpty()) {
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseSender().send("Subject ID not provided");
            return;
        }

        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();

        try {
            long subjectId = Long.parseLong(subjectIdParam);

            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM subject_details WHERE subject_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    preparedStatement.setLong(1, subjectId);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            Map<String, Object> subjectMap = new HashMap<>();
                            subjectMap.put("subject_id", resultSet.getLong("subject_id"));
                            subjectMap.put("subject_name", resultSet.getString("subject_name"));
                            subjectMap.put("date_created", resultSet.getString("date_created"));
                            subjectMap.put("date_modified", resultSet.getString("date_modified"));

                            response.put("data", subjectMap);
                            exchange.setStatusCode(StatusCodes.OK);
                        } else {
                            response.put("error", "Subject not found");
                            exchange.setStatusCode(StatusCodes.NOT_FOUND);
                        }
                    }
                }

                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(gson.toJson(response));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.put("error", "Invalid subject ID format");
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("error", "Failed to fetch subject data from the database");
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        }
    }
}
