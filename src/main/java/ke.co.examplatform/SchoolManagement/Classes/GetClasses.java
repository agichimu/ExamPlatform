package ke.co.examplatform.SchoolManagement.Classes;

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

public class GetClasses implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        List<Map<String, Object>> classList = new ArrayList<>();

        String selectQuery = "SELECT * FROM class_details";

        try (
                Connection connection = ConnectionsXmlReader.getDbConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                Map<String, Object> classMap = new HashMap<>();
                classMap.put("class_id", resultSet.getLong("class_id"));
                classMap.put("class_name", resultSet.getString("class_name"));
                classMap.put("date_created", resultSet.getTimestamp("date_created"));
                classMap.put("date_modified", resultSet.getTimestamp("date_modified"));

                classList.add(classMap);
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(classList));
        } catch (SQLException e) {
            e.printStackTrace();
            String errorResponse = gson.toJson("Failed to fetch class data: " + e.getMessage());
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }finally {
            System.out.println("request finished");
        }
    }
}
