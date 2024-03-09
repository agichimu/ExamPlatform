package ke.co.examplatform.Users.Guardians.guardianroles;

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

public class GetGuardianRole implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String roleIdParam = exchange.getQueryParameters().getOrDefault("roleId", new ArrayDeque<>(List.of("role_id"))).getFirst();


        if (roleIdParam.isEmpty()) {
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseSender().send("Role ID not provided");
            return;
        }

        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();

        try {
            long roleId = Long.parseLong(roleIdParam);

            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM guardian_roles WHERE role_id = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    preparedStatement.setLong(1, roleId);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            Map<String, Object> roleMap = new HashMap<>();
                            roleMap.put("role_id", resultSet.getLong("role_id"));
                            roleMap.put("role_name", resultSet.getString("role_name"));
                            roleMap.put("date_created", resultSet.getString("date_created"));
                            roleMap.put("date_modified", resultSet.getString("date_modified"));

                            response.put("data", roleMap);
                            exchange.setStatusCode(StatusCodes.OK);
                        } else {
                            response.put("error", "Guardian role not found");
                            exchange.setStatusCode(StatusCodes.NOT_FOUND);
                        }
                    }
                }

                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(gson.toJson(response));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.put("error", "Invalid role ID format");
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("error", "Failed to fetch guardian role data from the database");
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        }
    }
}
