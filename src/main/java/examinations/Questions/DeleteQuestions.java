package examinations.Questions;

import QuerryManager.QueryManager;
import Rest.RestUtils;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.sql.SQLException;
import java.util.LinkedHashMap;

public class DeleteQuestions implements HttpHandler {

    private final QueryManager queryManager;

    public DeleteQuestions(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) {
        Gson gson = new Gson();

        String deleteQuery = null;
        try {
            String strQuestionId = RestUtils.getPathVar(exchange, "questionId");
            LinkedHashMap<String, Object> values = new LinkedHashMap<>();
            values.put("1", strQuestionId);

            deleteQuery = "DELETE FROM questions_details WHERE question_id = ?";
            int rowsAffected = queryManager.delete(deleteQuery, values);

            exchange.setStatusCode(200);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

            if (rowsAffected > 0) {
                exchange.getResponseSender().send(gson.toJson("Question deleted successfully"));
            } else {
                exchange.getResponseSender().send(gson.toJson("Failed to delete question"));
            }
        } catch (SQLException | ClassNotFoundException e) {
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            e.printStackTrace();
            exchange.getResponseSender().send(gson.toJson("Failed to delete question: " + e.getMessage()));
        }
        System.out.println("Delete Query: " + deleteQuery);
    }
}
