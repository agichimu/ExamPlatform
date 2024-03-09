package ke.co.examplatform.Rest;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.util.HashMap;

/**
 * FallBack class handles requests for URIs that are not found on the server.
 */
public class FallBack implements HttpHandler {

    /**
     * Handles an HTTP request for a URI not found on the server.
     *
     * @param exchange The HTTP server exchange object representing the request and response.
     */
    @Override
    public void handleRequest(HttpServerExchange exchange) {
        // Create a HashMap to store error information
        HashMap<String, String> errorMap = new HashMap<>();
        errorMap.put("error_code", "ERR110");
        errorMap.put("error", "URI " + exchange.getRequestURI() + " not found on server");

        // Convert error information to JSON format
        Gson gson = new Gson();
        String strJsonResponse = gson.toJson(errorMap);

        // Set response status code to 404 (Not Found)
        exchange.setStatusCode(StatusCodes.NOT_FOUND);

        // Set content type header to indicate JSON response
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

        // Send JSON response with error information
        exchange.getResponseSender().send(strJsonResponse);
    }
}
