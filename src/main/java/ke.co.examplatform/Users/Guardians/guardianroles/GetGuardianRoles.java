package ke.co.examplatform.Users.Guardians.guardianroles;

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

public class GetGuardianRoles implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        List<Map<String, Object>> roleList = new ArrayList<>();

        String selectQuery = "SELECT * FROM guardian_roles";

        try (
                Connection connection = ConnectionsXmlReader.getDbConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                Map<String, Object> roleMap = new HashMap<>();
                roleMap.put("role_id", resultSet.getLong("role_id"));
                roleMap.put("role_name", resultSet.getString("role_name"));
                roleMap.put("date_created", resultSet.getTimestamp("date_created"));
                roleMap.put("date_modified", resultSet.getTimestamp("date_modified"));

                roleList.add(roleMap);
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(roleList));
        } catch (SQLException e) {
            e.printStackTrace();
            String errorResponse = gson.toJson("Failed to fetch guardian role data: " + e.getMessage());
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        } finally {
            System.out.println("request finished");
        }
    }
}
