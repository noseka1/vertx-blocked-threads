import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.ext.web.client.WebClient;

public class VertxBlockedThreads {

    public static void main(String[] args) {
        System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory.class.getName());
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MyServer());
    }
}

class MyServer extends AbstractVerticle {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyServer.class);
    private static final int LISTEN_PORT = 8080;

    @Override
    public void start(Future<Void> fut) {
        WebClient vertxClient = WebClient.create(vertx);
        Client jerseyClient = JerseyClientFactory.create();

        vertx.createHttpServer().requestHandler(new MyHandler(vertxClient, jerseyClient)).listen(LISTEN_PORT,
                result -> {
                    if (result.succeeded()) {
                        LOGGER.info("Server listening on port {}", LISTEN_PORT);
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
    }
}

class MyHandler implements Handler<HttpServerRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyHandler.class);

    private static final String URL = "http://zihadlo/hello.html";

    WebClient vertxClient;
    Client jerseyClient;

    public MyHandler(WebClient vertxClient, Client jerseyClient) {
        this.vertxClient = vertxClient;
        this.jerseyClient = jerseyClient;
    }

    @Override
    public void handle(HttpServerRequest event) {

        long startTime = System.nanoTime();

        Buffer content = downloadUrlJersey(URL);
        event.response().end(content);

        long endTime = System.nanoTime();
        LOGGER.info("Request processed in " + ((endTime - startTime) / 1_000) + " us");
    }

    public Buffer downloadUrlJersey(String url) {
        Response response = jerseyClient.target(url).request().get();
        InputStream is = response.readEntity(InputStream.class);
        return readInputStream(is);
    }

    private Buffer readInputStream(InputStream is) {
        Buffer content = Buffer.buffer();
        try {
            byte[] buf = new byte[10_000];
            int count = 0;
            while ((count = is.read(buf, 0, buf.length)) != -1) {
                content.appendBytes(buf, 0, count);
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read from input stream", e);
        }
        return content;
    }
}