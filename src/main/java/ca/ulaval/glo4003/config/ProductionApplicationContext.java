package ca.ulaval.glo4003.config;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.api.HealthResource;
import ca.ulaval.glo4003.repul.http.CORSResponseFilter;

public class ProductionApplicationContext implements ApplicationContext {
    private static final int PORT = 8080;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductionApplicationContext.class);

    private static HealthResource createHealthResource() {
        LOGGER.info("Setup health resource");

        return new HealthResource();
    }

    @Override
    public ResourceConfig initializeResourceConfig() {
        LOGGER.info("Setup resources (API)");
        HealthResource healthResource = createHealthResource();

        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(healthResource).to(HealthResource.class);
            }
        };

        return new ResourceConfig().packages("ca.ulaval.glo4003.repul.api").register(binder).register(new CORSResponseFilter());
    }

    public String getURI() {
        return String.format("http://localhost:%s/", PORT);
    }
}
