package ca.ulaval.glo4003.commons.fixture;

import java.net.URI;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;

import ca.ulaval.glo4003.config.ApplicationContext;

public class ServerFixture {
    private final Server server;

    public ServerFixture(ApplicationContext applicationContext) {
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
