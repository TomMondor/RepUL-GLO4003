package ca.ulaval.glo4003.repul.config;

import org.glassfish.jersey.server.ResourceConfig;

public interface ApplicationContext {
    ResourceConfig initializeResourceConfig();

    String getURI();
}
