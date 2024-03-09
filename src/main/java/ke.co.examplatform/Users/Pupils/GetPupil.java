package ke.co.examplatform.Users.Pupils;

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

public class GetPupil implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {

        String pupilIdParam = exchange.getQueryParameters().getOrDefault("pupilId", new ArrayDeque<>(List.of("pupil_id"))).getFirst();

        if (pupilIdParam.isEmpty()) {
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseSender().send("Pupil ID not provided");
            return;
        }

        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();

        try {
            long pupilId = Long.parseLong(pupilIdParam);

            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM pupils_details WHERE pupil_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    preparedStatement.setLong(1, pupilId);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            Map<String, Object> pupilMap = new HashMap<>();
                            pupilMap.put("pupil_id", resultSet.getLong("pupil_id"));
                            pupilMap.put("first_name", resultSet.getString("first_name"));
                            pupilMap.put("last_name", resultSet.getString("last_name"));
                            pupilMap.put("date_of_birth", resultSet.getString("date_of_birth"));
                            pupilMap.put("gender_id", resultSet.getInt("gender_id"));
                            pupilMap.put("class_id", resultSet.getLong("class_id"));
                            pupilMap.put("date_created", resultSet.getString("date_created"));
                            pupilMap.put("date_modified", resultSet.getString("date_modified"));

                            response.put("data", pupilMap);
                            exchange.setStatusCode(StatusCodes.OK);
                        } else {
                            response.put("error", "Pupil not found");
                            exchange.setStatusCode(StatusCodes.NOT_FOUND);
                        }
                    }
                }

                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(gson.toJson(response));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.put("error", "Invalid pupil ID format");
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("error", "Failed to fetch pupil data from the database");
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        }
    }
}
