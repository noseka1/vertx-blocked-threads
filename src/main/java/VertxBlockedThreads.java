import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.SLF4JLogDelegateFactory;

public class VertxBlockedThreads {

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory.class.getName());
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new HttpServer());
    }
}

class HttpServer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServer.class);
    private static final int LISTEN_PORT = 8080;
    private static final String URL = "http://www.example.com/hello.html";

    @Override
    public void start(Future<Void> fut) {
        Handler<HttpServerRequest> handler;

        if (System.getProperty("useVertxClient") == null) {
            handler = new RequestHandler1(URL);
        }
        else {
            handler = new RequestHandler2(URL, vertx);
        }
        LOGGER.info("Using {} request handler", handler.getClass());

        vertx.createHttpServer().requestHandler(handler).listen(LISTEN_PORT, result -> {
            if (result.succeeded()) {
                LOGGER.info("Server listening on port {}", LISTEN_PORT);
                fut.complete();
            } else {
                fut.fail(result.cause());
            }
        });
    }
}