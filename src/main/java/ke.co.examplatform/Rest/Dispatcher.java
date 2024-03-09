package ke.co.examplatform.Rest;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Dispatcher class acts as a handler for routing requests to different HTTP handlers.
 * It allows asynchronous dispatching of requests to handlers.
 */
public final class Dispatcher implements HttpHandler {
    private HttpHandler handler;

    /**
     * Constructs a Dispatcher with a provided root HTTP handler.
     *
     * @param handler The root HTTP handler to which requests will be dispatched.
     */
    public Dispatcher(HttpHandler handler) {
        this.handler = handler;
    }

    /**
     * Constructs a Dispatcher without any initial root HTTP handler.
     */
    public Dispatcher() {
        this(null);
    }

    /**
     * Handles an HTTP request by dispatching it to the root handler if the request is in an I/O thread.
     * @param exchange The HTTP server exchange object representing the request and response.
     * @throws Exception If an exception occurs during request handling.
     */
    @Deprecated
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        if (exchange.isInIoThread()) {
            exchange.dispatch(this.handler);
        }
    }

    /**
     * Retrieves the root HTTP handler.
     * @return The root HTTP handler.
     */
    public HttpHandler getHandler() {
        return this.handler;
    }

    /**
     * Sets the root HTTP handler.
     * @param rootHandler The root HTTP handler to set.
     * @return The Dispatcher instance.
     */
    public Dispatcher setRootHandler(HttpHandler rootHandler) {
        this.handler = rootHandler;
        return this;
    }

    /**
     * Wrapper class that wraps the Dispatcher as a HandlerWrapper.
     */
    private static class Wrapper implements HandlerWrapper {
        private Wrapper() {
        }

        /**
         * Wraps the provided HTTP handler with a Dispatcher instance.
         * @param handler The HTTP handler to wrap.
         * @return A new Dispatcher instance wrapping the provided handler.
         */
        public HttpHandler wrap(HttpHandler handler) {
            return new Dispatcher(handler);
        }
    }

    /**
     * Builder class for constructing Dispatcher instances.
     */
    public static class Builder implements HandlerBuilder {
        public Builder() {
        }

        /**
         * Gets the name of the handler.
         * @return The name of the handler.
         */
        public String name() {
            return "dispatcher";
        }

        /**
         * Gets the parameters required by the builder.
         * @return A map of parameters required by the builder.
         */
        public Map<String, Class<?>> parameters() {
            return Collections.emptyMap();
        }

        /**
         * Gets the required parameters for the builder.
         * @return A set of required parameters.
         */
        public Set<String> requiredParameters() {
            return Collections.emptySet();
        }

        /**
         * Gets the default parameter value.
         * @return The default parameter value.
         */
        public String defaultParameter() {
            return null;
        }

        /**
         * Builds a Wrapper instance.
         * @param config The configuration parameters.
         * @return A new Wrapper instance.
         */
        public HandlerWrapper build(Map<String, Object> config) {
            return new Wrapper();
        }
    }
}
