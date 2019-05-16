import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;

public class RequestHandler2 implements Handler<HttpServerRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler2.class);
    private final String url;
    HttpRequest<Buffer> request;

    public RequestHandler2(String url, Vertx vertx) {
        this.url = url;
        WebClient vertxClient = WebClient.create(vertx);
        request = vertxClient.getAbs(url);
    }

    @Override
    public void handle(HttpServerRequest event) {
        request.send(ar -> {
            if (ar.succeeded()) {
                HttpResponse<Buffer> response = ar.result();
                event.response().end(response.body());
            } else {
                LOGGER.error("Failed to download {}", url, ar.cause());
            }
        });
    }

}