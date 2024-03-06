package ke.co.examplatform.examinations;

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

public class GetExaminations implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        List<Map<String, Object>> examinationList = new ArrayList<>();

        try {
            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM examination_details";

                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            Map<String, Object> examinationMap = new HashMap<>();
                            examinationMap.put("examination_id", resultSet.getLong("examination_id"));
                            examinationMap.put("examination_name", resultSet.getString("examination_name"));
                            examinationMap.put("instructions", resultSet.getString("instructions"));
                            examinationMap.put("teacher_id", resultSet.getLong("teacher_id"));
                            examinationMap.put("subject_id", resultSet.getInt("subject_id"));
                            examinationMap.put("examination_time", resultSet.getString("examination_time"));
                            examinationMap.put("date_created", resultSet.getString("date_created"));
                            examinationMap.put("date_modified", resultSet.getString("date_modified"));
                            examinationList.add(examinationMap);
                        }
                    }
                }
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(examinationList));
        } catch (SQLException e) {
            e.printStackTrace();
            String errorResponse = "Failed to fetch examination data from the database";
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }
}
