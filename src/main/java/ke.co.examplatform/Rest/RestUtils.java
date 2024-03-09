package ke.co.examplatform.Rest;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;
import io.undertow.util.URLUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.HashMap;

/**
 * Utility class for handling RESTful operations.
 */
public class RestUtils {

    /**
     * Reads the request body from the HTTP server exchange.
     *
     * @param exchange The HTTP server exchange object representing the request and response.
     * @return The request body as a string.
     */
    public static String getRequestBody(HttpServerExchange exchange) {

        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();

        try {
            exchange.startBlocking();
            reader = new BufferedReader(new InputStreamReader(exchange.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return builder.toString();
    }

    /**
     * Retrieves the value of a path variable from the HTTP server exchange.
     *
     * @param exchange   The HTTP server exchange object representing the request and response.
     * @param pathVarId  The identifier of the path variable.
     * @return The value of the path variable.
     */
    public static String getPathVar(HttpServerExchange exchange, String pathVarId) {

        PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        StringBuilder builder = new StringBuilder();

        if (pathMatch.getParameters().get(pathVarId) == null) {
            return null;
        }

        URLUtils.decode(pathMatch.getParameters().get(pathVarId), StandardCharsets.UTF_8.name(), true, builder);
        return builder.toString();
    }

    /**
     * Retrieves the value of a query parameter from the HTTP server exchange.
     *
     * @param exchange The HTTP server exchange object representing the request and response.
     * @param key      The key of the query parameter.
     * @return The value of the query parameter.
     */
    public static String getQueryParam(HttpServerExchange exchange, String key) {
        Deque<String> param = exchange.getQueryParameters().get(key);
        String paramStr = null;

        if (param != null && !param.getFirst().equals("")) {
            paramStr = param.getFirst();
            paramStr = URLDecoder.decode(paramStr, StandardCharsets.UTF_8);
        }

        return paramStr;
    }

    /**
     * Retrieves multiple query parameters from the HTTP server exchange.
     *
     * @param exchange The HTTP server exchange object representing the request and response.
     * @param keys     The keys of the query parameters to retrieve.
     * @return A map containing the retrieved query parameters.
     */
    public static HashMap<String, String> getQueryParams(HttpServerExchange exchange, String... keys) {

        HashMap<String, String> params = new HashMap<>();
        Deque<String> param = null;

        for (String key : keys) {
            param = exchange.getQueryParameters().get(key);

            if (param != null && !param.getFirst().isEmpty()) {
                String paramStr = param.getFirst();
                paramStr = URLDecoder.decode(paramStr, StandardCharsets.UTF_8);
                params.put(key, paramStr);
            }
        }

        return params;
    }

}