import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.apache.connector.ApacheClientProperties;
import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;

public class JerseyClientFactory {

    private JerseyClientFactory() {
    }

    public static Client create() {

        ClientConfig clientConfig = new ClientConfig();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);

        clientConfig.property(ApacheClientProperties.CONNECTION_MANAGER, connectionManager);

        clientConfig.connectorProvider(new ApacheConnectorProvider());
        return ClientBuilder.newClient(clientConfig);
    }
}
