package ke.co.examplatform.examinations.Answers;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import ke.co.examplatform.QuerryManager.QueryManager;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class GetAnswer implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String answerIdParam = exchange.getQueryParameters().getOrDefault("answerId", new ArrayDeque<>(List.of("answer_id"))).getFirst();

        if (answerIdParam.isEmpty()) {
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseSender().send("Answer ID not provided");
            return;
        }

        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();

        try {
            long answerId = Long.parseLong(answerIdParam);

            QueryManager queryManager = new QueryManager();
            String selectQuery = "SELECT * FROM answer_detail WHERE answer_id = ?";
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("1", answerId);

            ResultSet resultSet = (ResultSet) queryManager.select(selectQuery, values);

            if (resultSet.next()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int count = metaData.getColumnCount();
                Map<String, Object> answerMap = new HashMap<>();

                for (int i = 1; i <= count; i++) {
                    answerMap.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                }

                response.put("data", answerMap);
                exchange.setStatusCode(StatusCodes.OK);
            } else {
                response.put("error", "Answer not found");
                exchange.setStatusCode(StatusCodes.NOT_FOUND);
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.put("error", "Invalid answer ID format");
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.put("error", "Failed to fetch answer data from the database");
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        }
    }
}
