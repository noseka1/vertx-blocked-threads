import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class RequestHandler1 implements Handler<HttpServerRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler1.class);
    String url;
    WebTarget target;

    public RequestHandler1(String url) {
        this.url = url;
        target = JerseyClientFactory.create().target(url);
    }

    @Override
    public void handle(HttpServerRequest event) {

        try {
            Response response = target.request().get();
            InputStream is = response.readEntity(InputStream.class);
            Buffer content = readInputStream(is);
            event.response().end(content);
        } catch (Exception e) {
            LOGGER.error("Failed to download {}", url, e);
        }
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
