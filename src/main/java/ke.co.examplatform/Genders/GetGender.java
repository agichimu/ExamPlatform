package ke.co.examplatform.Genders;

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

public class GetGender implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {

        String genderIdParam = exchange.getQueryParameters().getOrDefault("genderId", new ArrayDeque<>(List.of("gender_id"))).getFirst();

        if (genderIdParam.isEmpty()) {
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseSender().send("Gender ID not provided");
            return;
        }

        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();

        try {
            long genderId = Long.parseLong(genderIdParam);

            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM genders WHERE gender_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    preparedStatement.setLong(1, genderId);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            Map<String, Object> genderMap = new HashMap<>();
                            genderMap.put("gender_id", resultSet.getLong("gender_id"));
                            genderMap.put("gender_name", resultSet.getString("gender_name"));
                            genderMap.put("date_created", resultSet.getString("date_created"));
                            genderMap.put("date_modified", resultSet.getString("date_modified"));

                            response.put("data", genderMap);
                            exchange.setStatusCode(StatusCodes.OK);
                        } else {
                            response.put("error", "Gender not found");
                            exchange.setStatusCode(StatusCodes.NOT_FOUND);
                        }
                    }
                }

                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(gson.toJson(response));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.put("error", "Invalid gender ID format");
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("error", "Failed to fetch gender data from the database");
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        }
    }
}