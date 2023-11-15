package Users.Guardians;

import QuerryManager.QueryManager;
import Rest.RestUtils;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.sql.SQLException;
import java.util.*;

public class GetGuardian implements HttpHandler {

    private final QueryManager queryManager;

    public GetGuardian(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        String guardianId = RestUtils.getPathVar(exchange, "guardianId");

        if (guardianId != null) {
            try {
                Map<String, Object> guardianMap;
                guardianMap = Collections.unmodifiableMap(getGuardian(guardianId));
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(gson.toJson(guardianMap));
            } catch (SQLException e) {
                e.printStackTrace();
                String errorResponse = "Failed to fetch guardian";
                exchange.setStatusCode(500);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(errorResponse);
            }
        } else {
            String errorResponse = "Guardian ID not provided";
            exchange.setStatusCode(400);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }

    private Map<String, Object> getGuardian(String guardianId) throws SQLException {
        Map<String, Object> guardianMap = new HashMap<>();

        String selectQuery = "SELECT * FROM guardian_details WHERE guardian_id = ?";

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("guardianId", guardianId);

        try {
            List<LinkedHashMap<String, Object>> results = queryManager.select(selectQuery, paramMap);

            if (!results.isEmpty()) {
                guardianMap = results.get(0);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return guardianMap;
    }

}
