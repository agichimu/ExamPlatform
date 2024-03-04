package ke.co.examplatform.SchoolManagement.Subjects;

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

public class GetSubjects implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        List<Map<String, Object>> subjectList = new ArrayList<>();

        String selectQuery = "SELECT * FROM subject_details";

        try (
                Connection connection = ConnectionsXmlReader.getDbConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            while (resultSet.next()) {
                Map<String, Object> subjectMap = new HashMap<>();
                subjectMap.put("subject_id", resultSet.getLong("subject_id"));
                subjectMap.put("subject_name", resultSet.getString("subject_name"));
                subjectMap.put("subject_code", resultSet.getInt("subject_code"));
                subjectMap.put("department_id", resultSet.getLong("department_id"));
                subjectMap.put("date_created", resultSet.getTimestamp("date_created"));
                subjectMap.put("date_modified", resultSet.getTimestamp("date_modified"));

                subjectList.add(subjectMap);
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(subjectList));
        } catch (SQLException e) {
            e.printStackTrace();
            String errorResponse = gson.toJson("Failed to fetch subject data: " + e.getMessage());
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        } finally {
            System.out.println("request finished");
        }
    }
}
