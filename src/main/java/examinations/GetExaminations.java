package examinations;

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

public class GetExaminations implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        List<Map<String, Object>> examList = new ArrayList<>();

        try {
            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM examination_details";

                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            Map<String, Object> examMap = new HashMap<>();
                            examMap.put("examination_id", resultSet.getLong("examination_id"));
                            examMap.put("instructions", resultSet.getString("instructions"));
                            examMap.put("teacher_id", resultSet.getInt("teacher_id"));
                            examMap.put("examination_name", resultSet.getString("examination_name"));
                            examMap.put("subject_id", resultSet.getInt("subject_id"));
                            examMap.put("examination_time", resultSet.getString("examination_time"));
                            examMap.put("question_id", resultSet.getLong("question_id"));
                            examMap.put("date_created", resultSet.getString("date_created"));
                            examMap.put("date_modified", resultSet.getString("date_modified"));

                            examList.add(examMap);
                        }
                    }
                }
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(examList));
        } catch (SQLException e) {
            e.printStackTrace();
            String errorResponse = "Failed to fetch exam data from the database";
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }
}
