package ca.ulaval.glo4003.repul;

import java.net.URI;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.config.ApplicationContext;

public class RepULServer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepULServer.class);

    private final ApplicationContext applicationContext;

    public RepULServer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void run() {
        Server server = initializeServer();
        start(server);
    }

    private Server initializeServer() {
        LOGGER.info("Setup http server");
        return JettyHttpContainerFactory.createServer(URI.create(applicationContext.getURI()), applicationContext.initializeResourceConfig());
    }

    private void start(Server server) {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    LOGGER.info("Shutting down the application...");
                    server.stop();
                    LOGGER.info("Done, exit.");
                } catch (Exception e) {
                    LOGGER.error("Error shutting down the server", e);
                }
            }));

            LOGGER.info("Application started.%nStop the application using CTRL+C");

            // block and wait shut down signal, like CTRL+C
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            LOGGER.error("Error starting up the server", e);
        }
    }
}
