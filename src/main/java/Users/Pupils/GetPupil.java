package Users.Pupils;

import QuerryManager.QueryManager;
import Rest.RestUtils;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.sql.SQLException;
import java.util.*;

public class GetPupil implements HttpHandler  {

    private final QueryManager queryManager;

    public GetPupil(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        Gson gson = new Gson();
        String pupilId = RestUtils.getPathVar(exchange, "pupilId");

        if (pupilId != null) {
            try {
                Map<String, Object> pupilMap;
                pupilMap = Collections.unmodifiableMap(getPupil(pupilId));
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(gson.toJson(pupilMap));
            } catch (SQLException e) {
                e.printStackTrace();
                String errorResponse = "Failed to fetch pupil";
                exchange.setStatusCode(500);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(errorResponse);
            }
        } else {
            String errorResponse = "Pupil ID not provided";
            exchange.setStatusCode(400);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(errorResponse);
        }
    }

    private Map<String, Object> getPupil(String pupilId ) throws SQLException {
        Map<String, Object> pupilMap  = new HashMap<>();

        //String selectQuery = "SELECT * FROM pupils_details WHERE pupil_id = ?";
        String selectQuery = "SELECT * FROM pupils_details WHERE gender = 'male'";


        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("pupilId", pupilId );

        try {
            List<LinkedHashMap<String, Object>> results = queryManager.select(selectQuery, paramMap);

            if (!results.isEmpty()) {
                pupilMap  = results.get(0);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return pupilMap ;
    }

}
