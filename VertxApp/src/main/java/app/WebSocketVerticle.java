package app;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

import java.util.Date;

public class WebSocketVerticle extends AbstractVerticle{

    @Override
    public void start(Future<Void> future){
        vertx.
            createHttpServer().websocketHandler(ws -> {
            if (!ws.path().equals("/socket")) {
                ws.reject();
                return;
            }
            ws.handler(data -> ws.writeFinalTextFrame(String.format("[%s]: %s.",
                new Date().toString(), data.toString())));
        }).listen(8090, result -> {
            if (result.succeeded()) {
                future.complete();
            } else {
                future.fail(result.cause());
            }
        });
    }
}