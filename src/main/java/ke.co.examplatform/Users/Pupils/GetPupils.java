package ke.co.examplatform.Users.Pupils;

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

public class GetPupils implements HttpHandler {


    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        List<Map<String, Object>> pupilList = new ArrayList<>();

        try {
            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM pupils_details";

                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            Map<String, Object> pupilMap = new HashMap<>();
                            pupilMap.put("pupil_id", resultSet.getLong("pupil_id"));
                            pupilMap.put("first_name", resultSet.getString("first_name"));
                            pupilMap.put("last_name", resultSet.getString("last_name"));
                            pupilMap.put("date_of_birth", resultSet.getString("date_of_birth"));
                            pupilMap.put("gender_id", resultSet.getInt("gender_id"));
                            pupilMap.put("class_id", resultSet.getLong("class_id"));
                            pupilMap.put("date_created", resultSet.getString("date_created"));
                            pupilMap.put("date_modified", resultSet.getString("date_modified"));

                            pupilList.add(pupilMap);
                        }
                    }
                }
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(pupilList));
        } catch (SQLException e) {
            e.printStackTrace();
            String errorResponse = "Failed to fetch pupil data from the database";
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }
}
