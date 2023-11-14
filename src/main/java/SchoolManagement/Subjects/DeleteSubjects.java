package SchoolManagement.Subjects;

import QuerryManager.QueryManager;
import Rest.RestUtils;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class DeleteSubjects implements HttpHandler {

    private final QueryManager queryManager;

    public DeleteSubjects(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        Gson gson = new Gson();

        String deleteQuery = null;
        try {
            String strSubjectId = RestUtils.getPathVar(exchange, "subjectId");
            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", strSubjectId);

            deleteQuery = "DELETE FROM subject_details WHERE subject_id = ?";
            int rowsAffected = queryManager.delete(deleteQuery, values);

            exchange.setStatusCode(200);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

            if (rowsAffected > 0) {
                exchange.getResponseSender().send(gson.toJson("Subject deleted successfully"));
            } else {
                exchange.getResponseSender().send(gson.toJson("Failed to delete subject"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            exchange.setStatusCode(500);
            e.printStackTrace();
            exchange.getResponseSender().send(gson.toJson("Failed to delete subject: " + e.getMessage()));
        }
        System.out.println("Delete Query: " + deleteQuery);

    }
}
