package Rest;

import examinations.CreateExaminations;
import io.undertow.Handlers;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;

public class Routes {
    public static RoutingHandler examinations(){
        return Handlers.routing()
               /* .get("", new Dispatcher(new createExaminations()))*/
               // .get("/{userId}", new Dispatcher(new GetUser()))
                .post("", new BlockingHandler(new CreateExaminations()));
                //.add(Methods.PATCH, "/{userId}", new BlockingHandler(new UpdateUser()))
                //.delete("/{userId}", new Dispatcher(new DeleteUser()))
                //.add(Methods.OPTIONS, "/*", new CorsHandler())
                //.setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                //.setFallbackHandler(new Dispatcher(new FallBack()));

    }
}
