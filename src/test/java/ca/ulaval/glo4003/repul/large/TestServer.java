package ca.ulaval.glo4003.repul.large;

import java.net.URI;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;

import ca.ulaval.glo4003.config.ApplicationContext;
import ca.ulaval.glo4003.config.DevApplicationContext;

public class TestServer {
    private final Server server;

    public TestServer() {
        ApplicationContext applicationContext = new DevApplicationContext();
        URI uri = URI.create(applicationContext.getURI());
        server = JettyHttpContainerFactory.createServer(uri, applicationContext.initializeResourceConfig());
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }
}
