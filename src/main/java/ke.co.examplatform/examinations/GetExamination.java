package ke.co.examplatform.examinations;

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

public class GetExamination implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String examinationIdParam = exchange.getQueryParameters().getOrDefault("examinationId", new ArrayDeque<>(List.of("examination_id"))).getFirst();

        if (examinationIdParam.isEmpty()) {
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseSender().send("Examination ID not provided");
            return;
        }

        Gson gson = new Gson();
        Map<String, Object> response = new HashMap<>();

        try {
            long examinationId = Long.parseLong(examinationIdParam);

            QueryManager queryManager = new QueryManager();
            String selectQuery = "SELECT * FROM examination_details WHERE examination_id = ?";
            Map<String, Object> values = new LinkedHashMap<>();
            values.put("1", examinationId);

            ResultSet resultSet = (ResultSet) queryManager.select(selectQuery, values);

            if (resultSet.next()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int count = metaData.getColumnCount();
                Map<String, Object> examinationMap = new HashMap<>();

                for (int i = 1; i <= count; i++) {
                    examinationMap.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                }

                response.put("data", examinationMap);
                exchange.setStatusCode(StatusCodes.OK);
            } else {
                response.put("error", "Examination not found");
                exchange.setStatusCode(StatusCodes.NOT_FOUND);
            }

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.put("error", "Invalid examination ID format");
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            response.put("error", "Failed to fetch examination data from the database");
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(gson.toJson(response));
        }
    }
}
