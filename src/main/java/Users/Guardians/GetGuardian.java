/*
package Users.Guardians;

import Utilities.ConnectionsXmlReader;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class GetGuardian implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        Map<String, Object> guardianDetails = new HashMap<>();

        try {
            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM guardian_details WHERE guardian_id = ?";

                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            guardianDetails.put("guardian_id", resultSet.getLong("guardian_id"));
                            guardianDetails.put("first_name", resultSet.getString("first_name"));
                            guardianDetails.put("second_name", resultSet.getString("second_name"));
                            guardianDetails.put("surname", resultSet.getString("surname"));
                            guardianDetails.put("phone_number", resultSet.getString("phone_number"));
                            guardianDetails.put("email_address", resultSet.getString("email_address"));
                            guardianDetails.put("gender", resultSet.getString("gender"));
                            guardianDetails.put("date_created", resultSet.getString("date_created"));
                            guardianDetails.put("date_modified", resultSet.getString("date_modified"));
                        }
                    }
                }
            }

            if (!guardianDetails.isEmpty()) {
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(gson.toJson(guardianDetails));
            } else {
                String errorResponse = "Guardian with ID 1 not found";
                exchange.setStatusCode(404);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(errorResponse);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String errorResponse = "Failed to fetch guardian data from the database";
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }
}
*/
