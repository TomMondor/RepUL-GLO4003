package ca.ulaval.glo4003.repul.config.initializer;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.config.seed.Seed;
import ca.ulaval.glo4003.repul.config.seed.SeedFactory;
import ca.ulaval.glo4003.repul.lockerauthorization.api.LockerAuthorizationEventHandler;
import ca.ulaval.glo4003.repul.lockerauthorization.api.LockerAuthorizationResource;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;
import ca.ulaval.glo4003.repul.lockerauthorization.domain.LockerAuthorizationSystemPersister;
import ca.ulaval.glo4003.repul.lockerauthorization.infrastructure.InMemoryLockerAuthorizationSystemPersister;
import ca.ulaval.glo4003.repul.lockerauthorization.middleware.ApiKeyGuard;

public class LockerAuthorizationContextInitializer implements ContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestApplicationContext.class);

    private final RepULEventBus eventBus;
    private final SeedFactory seedFactory;
    private final LockerAuthorizationSystemPersister lockerAuthorizationSystemPersister = new InMemoryLockerAuthorizationSystemPersister();

    public LockerAuthorizationContextInitializer(RepULEventBus eventBus, SeedFactory seedFactory) {
        this.eventBus = eventBus;
        this.seedFactory = seedFactory;
    }

    @Override
    public void initialize(ResourceConfig resourceConfig) {
        LockerAuthorizationService lockerAuthorizationService = new LockerAuthorizationService(eventBus, lockerAuthorizationSystemPersister);
        createLockerAuthorizationEventHandler(lockerAuthorizationService);
        ApiKeyGuard apiKeyGuard = new ApiKeyGuard();
        populate();

        LockerAuthorizationResource lockerAuthorizationResource = new LockerAuthorizationResource(lockerAuthorizationService);

        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(lockerAuthorizationResource).to(LockerAuthorizationResource.class);
            }
        };

        resourceConfig.register(binder).register(apiKeyGuard);
    }

    private void createLockerAuthorizationEventHandler(LockerAuthorizationService lockerAuthorizationService) {
        LockerAuthorizationEventHandler lockerAuthorizationEventHandler = new LockerAuthorizationEventHandler(lockerAuthorizationService);
        eventBus.register(lockerAuthorizationEventHandler);
    }

    private void populate() {
        Seed seed = seedFactory.createLockerAuthorizationSeed(lockerAuthorizationSystemPersister);
        seed.populate();
    }
}
