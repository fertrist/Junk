package app;

import io.vertx.core.Vertx;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class WebSocketVerticleTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(WebSocketVerticle.class.getName(),
            context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void testWebSocket(TestContext context) {
        final Async async = context.async();

        vertx.createHttpClient().websocket(8090, "localhost", "/socket", ws -> {
            ws.writeFinalTextFrame("Hello server!");
            ws.handler(data -> System.out.println("Received : " + data.toString()));
            async.complete();
        });
    }
}