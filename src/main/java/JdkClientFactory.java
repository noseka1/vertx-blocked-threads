import java.net.MalformedURLException;
import java.net.URL;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class JdkClientFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdkClientFactory.class);

    private JdkClientFactory() {
    }

    public static URL create(String url) {
        URL client = null;
        try {
            client = new URL(url);
        } catch (MalformedURLException e) {
            LOGGER.error("Bad URL", e);
        }
        return client;
    }
}
