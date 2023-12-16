package ca.ulaval.glo4003.repul.config.initializer;

import org.glassfish.jersey.server.ResourceConfig;

public interface ContextInitializer {
    void initialize(ResourceConfig resourceConfig);
}
