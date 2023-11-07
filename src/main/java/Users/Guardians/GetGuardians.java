package Users.Guardians;

import Utilities.ConnectionsXmlReader;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetGuardians implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        List<Map<String, Object>> guardianList = new ArrayList<>();

        try {
            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM guardian_details";

                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            Map<String, Object> guardianMap = new HashMap<>();
                            guardianMap.put("guardian_id", resultSet.getLong("guardian_id"));
                            guardianMap.put("first_name", resultSet.getString("first_name"));
                            guardianMap.put("second_name", resultSet.getString("second_name"));
                            guardianMap.put("surname", resultSet.getString("surname"));
                            guardianMap.put("phone_number", resultSet.getString("phone_number"));
                            guardianMap.put("email_address", resultSet.getString("email_address"));
                            guardianMap.put("gender", resultSet.getString("gender"));
                            guardianMap.put("date_created", resultSet.getString("date_created"));
                            guardianMap.put("date_modified", resultSet.getString("date_modified"));

                            guardianList.add(guardianMap);
                        }
                    }
                }
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(guardianList));
        } catch (SQLException e) {
            e.printStackTrace();
            String errorResponse = "Failed to fetch guardian data from the database";
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }
}
