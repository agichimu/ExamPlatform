package ke.co.examplatform.Users.Pupils;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import ke.co.examplatform.QuerryManager.QueryManager;
import ke.co.examplatform.Rest.RestUtils;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class DeletePupils implements HttpHandler {

    private final QueryManager queryManager;

    public DeletePupils(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        Gson gson = new Gson();

        String deleteQuery = null;
        try {
            String strPupilId = RestUtils.getPathVar(exchange, "pupilId");
            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", strPupilId);

            deleteQuery = "DELETE FROM pupils_details WHERE pupil_id = ?";
            int rowsAffected = queryManager.delete(deleteQuery, values);

            exchange.setStatusCode(200);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

            if (rowsAffected > 0) {
                exchange.getResponseSender().send(gson.toJson("Pupil deleted successfully"));
            } else {
                exchange.getResponseSender().send(gson.toJson("Failed to delete pupil"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            exchange.setStatusCode(500);
            e.printStackTrace();
            exchange.getResponseSender().send(gson.toJson("Failed to delete pupil: " + e.getMessage()));
        }
        System.out.println("Delete Query: " + deleteQuery);
    }
}
