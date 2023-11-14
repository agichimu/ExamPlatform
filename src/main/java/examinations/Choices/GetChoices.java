package examinations.Choices;

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

public class GetChoices implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        List<Map<String, Object>> choicesList = new ArrayList<>();

        try {
            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM choices_details";

                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            Map<String, Object> choiceMap = new HashMap<>();
                            choiceMap.put("choice_id", resultSet.getLong("choice_id"));
                            choiceMap.put("choice_label", resultSet.getString("choice_label"));
                            choiceMap.put("choice_content", resultSet.getString("choice_content"));
                            choiceMap.put("is_right", resultSet.getBoolean("is_right"));
                            choiceMap.put("question_id", resultSet.getLong("question_id"));
                            choiceMap.put("date_created", resultSet.getString("date_created"));
                            choiceMap.put("date_modified", resultSet.getString("date_modified"));

                            choicesList.add(choiceMap);
                        }
                    }
                }
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(choicesList));
        } catch (SQLException e) {
            e.printStackTrace();
            String errorResponse = "Failed to fetch choices data from the database";
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }
}
