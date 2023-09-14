package ca.ulaval.glo4003.config;

import java.net.URI;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.Main;

public class SubscriptionServer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private final ApplicationContext applicationContext;

    public SubscriptionServer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void run() {
        try {
            LOGGER.info("Setup http server");
            final Server server = JettyHttpContainerFactory.createServer(
                URI.create(applicationContext.getURI()),
                applicationContext.initializeResourceConfig()
            );

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
