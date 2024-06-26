package ke.co.examplatform.Users.Guardians;

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

public class GetGuardian implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String guardianIdParam = exchange.getQueryParameters().getOrDefault("guardianId", new ArrayDeque<>(List.of("guardian_id"))).getFirst();


        if (guardianIdParam.isEmpty()) {
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseSender().send("Guardian ID not provided");
            return;
        }

        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();

        try {
            long guardianId = Long.parseLong(guardianIdParam);

            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM guardian_details WHERE guardian_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    preparedStatement.setLong(1, guardianId);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            Map<String, Object> guardianMap = new HashMap<>();
                            guardianMap.put("guardian_id", resultSet.getLong("guardian_id"));
                            guardianMap.put("first_name", resultSet.getString("first_name"));
                            guardianMap.put("surname", resultSet.getString("surname"));
                            guardianMap.put("phone_number", resultSet.getString("phone_number"));
                            guardianMap.put("gender_id", resultSet.getInt("gender_id"));
                            guardianMap.put("role_id", resultSet.getLong("role_id"));
                            guardianMap.put("date_created", resultSet.getString("date_created"));
                            guardianMap.put("date_modified", resultSet.getString("date_modified"));

                            response.put("data", guardianMap);
                            exchange.setStatusCode(StatusCodes.OK);
                        } else {
                            response.put("error", "Guardian not found");
                            exchange.setStatusCode(StatusCodes.NOT_FOUND);
                        }
                    }
                }

                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(gson.toJson(response));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.put("error", "Invalid guardian ID format");
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("error", "Failed to fetch guardian data from the database");
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        }
    }
}