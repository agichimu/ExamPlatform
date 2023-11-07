package examinations;

import QuerryManager.QueryManager;
import Rest.RestUtils;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class GetExamination implements HttpHandler {

    private final QueryManager queryManager;

    public GetExamination(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        String examinationId = RestUtils.getPathVar(exchange, "examinationId");

        try {
            assert examinationId != null;
            int examinationIdInt = Integer.parseInt(examinationId);

            String selectQuery = "SELECT * FROM examination_details WHERE examination_id = ?";

            LinkedHashMap<Integer, Object> paramMap = new LinkedHashMap<>();
            paramMap.put(1, examinationIdInt);

            var resultSet = queryManager.select(selectQuery, paramMap);

            if (resultSet != null && !resultSet.isEmpty()) {
                var examination = resultSet.get(0);

                //JSON response
                Gson gson = new Gson();
                String strJsonResponse = gson.toJson(examination);

                exchange.setStatusCode(200);

                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");


                exchange.getResponseSender().send(strJsonResponse);
            } else {
                exchange.setStatusCode(404);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send("Error: Examination not found");
            }
        } catch (NumberFormatException e) {
            exchange.setStatusCode(400);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: Invalid examinationId");
        } catch (SQLException | ClassNotFoundException e) {
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: Internal Server Error");
        }
    }
}
