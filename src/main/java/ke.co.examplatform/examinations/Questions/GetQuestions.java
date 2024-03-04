package ke.co.examplatform.examinations.Questions;

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

public class GetQuestions implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        List<Map<String, Object>> questionList = new ArrayList<>();

        try {
            try (Connection connection = ConnectionsXmlReader.getDbConnection()) {
                String selectQuery = "SELECT * FROM questions_details";

                try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            Map<String, Object> questionMap = new HashMap<>();
                            questionMap.put("question_id", resultSet.getLong("question_id"));
                            questionMap.put("question_layout", resultSet.getInt("question_layout"));
                            questionMap.put("question_text", resultSet.getString("question_text"));
                            questionMap.put("question_total_marks", resultSet.getInt("question_total_marks"));
                            questionMap.put("question_time", resultSet.getString("question_time"));
                            questionMap.put("examination_id", resultSet.getLong("examination_id"));
                            questionMap.put("question_type", resultSet.getString("question_type"));
                            questionMap.put("date_created", resultSet.getString("date_created"));
                            questionMap.put("date_modified", resultSet.getString("date_modified"));

                            questionList.add(questionMap);
                        }
                    }
                }
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(questionList));
        } catch (SQLException e) {
            e.printStackTrace();
            String errorResponse = "Failed to fetch question data from the database";
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }
}
