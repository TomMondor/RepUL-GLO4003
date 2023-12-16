package ca.ulaval.glo4003.repul.config.initializer;

import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.config.seed.Seed;
import ca.ulaval.glo4003.repul.config.seed.SeedFactory;
import ca.ulaval.glo4003.repul.notification.application.NotificationService;
import ca.ulaval.glo4003.repul.notification.domain.DeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.domain.NotificationSender;
import ca.ulaval.glo4003.repul.notification.domain.UserAccountRepository;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryDeliveryPersonAccountRepository;
import ca.ulaval.glo4003.repul.notification.infrastructure.InMemoryUserAccountRepository;

public class NotificationContextInitializer implements ContextInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationContextInitializer.class);

    private final NotificationSender notificationSender;
    private final RepULEventBus eventBus;
    private final SeedFactory seedFactory;
    private final UserAccountRepository userAccountRepository = new InMemoryUserAccountRepository();
    private final DeliveryPersonAccountRepository deliveryPersonAccountRepository = new InMemoryDeliveryPersonAccountRepository();

    public NotificationContextInitializer(NotificationSender notificationSender, RepULEventBus eventBus, SeedFactory seedFactory) {
        this.notificationSender = notificationSender;
        this.eventBus = eventBus;
        this.seedFactory = seedFactory;
    }

    @Override
    public void initialize(ResourceConfig resourceConfig) {
        LOGGER.info("Initializing notification context");
        NotificationService notificationService = new NotificationService(userAccountRepository, deliveryPersonAccountRepository, notificationSender);
        populate();
        eventBus.register(notificationService);
    }

    private void populate() {
        Seed seed = seedFactory.createNotificationSeed(userAccountRepository, deliveryPersonAccountRepository);
        seed.populate();
    }
}
