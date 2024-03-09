package ke.co.examplatform.Rest;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.util.HashMap;

/**
 * InvalidMethod class handles requests with invalid HTTP methods.
 */
public class InvalidMethod implements HttpHandler {

    /**
     * Handles an HTTP request with an invalid HTTP method.
     *
     * @param exchange The HTTP server exchange object representing the request and response.
     * @throws Exception If an exception occurs during request handling.
     */
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Create a HashMap to store error information
        HashMap<String, String> errorMap = new HashMap<>();
        errorMap.put("error_code", "ERR100");
        errorMap.put("error", "Method " + exchange.getRequestMethod() + " not allowed");

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
