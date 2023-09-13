package ca.ulaval.glo4003;

import java.net.URI;

import org.eclipse.jetty.server.Server;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jetty.JettyHttpContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.ws.api.HealthResource;
import ca.ulaval.glo4003.ws.http.CORSResponseFilter;

/**
 * RESTApi setup without using DI or spring.
 */
@SuppressWarnings("all")
public class TelephonyWsMain {
    public static final String BASE_URI = "http://localhost:8080/";
    private static final Logger LOGGER = LoggerFactory.getLogger(TelephonyWsMain.class);

    public static void main(String[] args) throws Exception {

        LOGGER.info("Setup resources (API)");
        HealthResource healthResource = createHealthResource();

        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(healthResource).to(HealthResource.class);
            }
        };

        final ResourceConfig config = new ResourceConfig();
        config.register(binder);
        config.register(new CORSResponseFilter());
        config.packages("ca.ulaval.glo4003.ws.api");

        try {
            LOGGER.info("Setup http server");
            final Server server = JettyHttpContainerFactory.createServer(URI.create(BASE_URI), config);

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
            LOGGER.error("Error startig up the server", e);
        }
    }

    private static HealthResource createHealthResource() {
        LOGGER.info("Setup health resource");

        return new HealthResource();
    }
}
