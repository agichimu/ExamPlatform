package ke.co.examplatform.Rest;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;

/**
 * Handles CORS (Cross-Origin Resource Sharing) headers for HTTP requests.
 */
public class CorsHandler implements HttpHandler {

    /**
     * Handles an HTTP request by setting CORS headers and status code.
     *
     * @param exchange The HTTP server exchange object representing the request and response.
     */
    @Override
    public void handleRequest(HttpServerExchange exchange) {
        // Set Access-Control-Allow-Origin header to allow requests from any origin
        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Origin"), "*");

        // Set Access-Control-Allow-Methods header to allow specific HTTP methods
        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Methods"),
                "POST, GET, OPTIONS, PUT, PATCH, DELETE");

        // Set Access-Control-Allow-Headers header to allow specific headers in requests
        exchange.getResponseHeaders().put(
                new HttpString("Access-Control-Allow-Headers"),
                "Content-Type,Accept,HandlerAuthorizationLayer,AuthToken,Authorization,RequestReference,UserId");

        // Set response status code to 200 (OK)
        exchange.setStatusCode(200);
    }
}
