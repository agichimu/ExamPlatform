package ke.co.examplatform.examinations;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.QuerryManager.QueryManager;
import ke.co.examplatform.Rest.RestUtils;

import java.sql.SQLException;
import java.util.*;

public class GetExamination implements HttpHandler  {

    private final QueryManager queryManager;

    public GetExamination(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        String examinationId = RestUtils.getPathVar(exchange, "examinationId");

        if (examinationId != null) {
            try {
                Map<String, Object> examinationMap;
                examinationMap = Collections.unmodifiableMap(getExamination(examinationId));
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(gson.toJson(examinationMap));
            } catch (SQLException e) {
                e.printStackTrace();
                String errorResponse = "Failed to fetch guardian";
                exchange.setStatusCode(500);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(errorResponse);
            }
        } else {
            String errorResponse = "Examination ID not provided";
            exchange.setStatusCode(400);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }

    private Map<String, Object> getExamination(String examinationId) throws SQLException {
        Map<String, Object> examinationMap = new HashMap<>();

        String selectQuery = "SELECT * FROM examination_details WHERE examination_id = ?";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("examinationId", examinationId);

        try {
            List<LinkedHashMap<String, Object>> results = queryManager.select(selectQuery, paramMap);

            if (!results.isEmpty()) {
                examinationMap = results.get(0);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return examinationMap;
    }

}
