package examinations;

import QuerryManager.QueryManager;
import Rest.RestUtils;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import com.google.gson.Gson;

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

                // Creating a JSON response
                Gson gson = new Gson();
                String strJsonResponse = gson.toJson(examination);

                // Setting the HTTP response status code to 200 (OK)
                exchange.setStatusCode(StatusCodes.OK);

                // Setting the response content type to "application/json"
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

                // Sending the JSON response to the client
                exchange.getResponseSender().send(strJsonResponse);
            } else {
                // Examination not found
                exchange.setStatusCode(StatusCodes.NOT_FOUND);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send("Error: Examination not found");
            }
        } catch (NumberFormatException e) {
            // Handle the case where an invalid examinationId is provided
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: Invalid examinationId");
        } catch (SQLException | ClassNotFoundException e) {
            // Handle database errors
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: Internal Server Error");
        }
    }
}
