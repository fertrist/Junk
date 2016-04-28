package app;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class HttpVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> future){
        vertx.
            createHttpServer()
            .requestHandler(r -> r.response().end("<h1>Hello from my Vert.x 3 application</h1>"))
            .listen(8080, result -> {
                if (result.succeeded()) {
                    future.complete();
                } else {
                    future.fail(result.cause());
                }
            });
    }
}