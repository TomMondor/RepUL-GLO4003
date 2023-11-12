package ca.ulaval.glo4003.repul.config.initializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.config.context.TestApplicationContext;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;

public class LockerAuthorizationContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestApplicationContext.class);

    public LockerAuthorizationService createLockerAuthorizationService(RepULEventBus eventBus) {
        LOGGER.info("Creating locker authorization service");
        LockerAuthorizationService service = new LockerAuthorizationService();
        eventBus.register(service);
        return service;
    }
}
