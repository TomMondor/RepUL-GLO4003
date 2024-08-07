package ca.ulaval.glo4003.repul.config.context;

import java.time.Duration;
import java.time.LocalTime;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.api.exception.mapper.CatchallExceptionMapper;
import ca.ulaval.glo4003.repul.commons.api.exception.mapper.ConstraintViolationExceptionMapper;
import ca.ulaval.glo4003.repul.commons.api.exception.mapper.NotFoundExceptionMapper;
import ca.ulaval.glo4003.repul.commons.api.exception.mapper.RepULExceptionMapper;
import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.config.Config;
import ca.ulaval.glo4003.repul.config.env.EnvParser;
import ca.ulaval.glo4003.repul.config.initializer.CookingContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.DeliveryContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.IdentityManagementContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.LockerAuthorizationContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.NotificationContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.SubscriptionContextInitializer;
import ca.ulaval.glo4003.repul.config.seed.SeedFactory;
import ca.ulaval.glo4003.repul.config.seed.TestSeedFactory;
import ca.ulaval.glo4003.repul.health.api.HealthResource;
import ca.ulaval.glo4003.repul.notification.infrastructure.LogNotificationSender;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.infrastructure.LogPaymentService;

public class TestApplicationContext implements ApplicationContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestApplicationContext.class);
    private static final int PORT = 8081;

    public TestApplicationContext() {
        LocalTime openingTime = LocalTime.of(9, 0);
        Duration durationToConfirm = Duration.ofDays(2);
        Config.initialize(openingTime, durationToConfirm);
    }

    @Override
    public ResourceConfig initializeResourceConfig() {
        EnvParser.setFilename(".envExample");

        RepULEventBus eventBus = new GuavaEventBus();
        SeedFactory seedFactory = new TestSeedFactory();

        PaymentService paymentService = new LogPaymentService();

        ResourceConfig resourceConfig = new ResourceConfig();

        LOGGER.info("Creating Health resource");
        HealthResource healthResource = new HealthResource();

        NotificationContextInitializer notificationContextInitializer = new NotificationContextInitializer(new LogNotificationSender(), eventBus, seedFactory);
        notificationContextInitializer.initialize(resourceConfig);

        IdentityManagementContextInitializer identityManagementContextInitializer = new IdentityManagementContextInitializer(eventBus, seedFactory);
        identityManagementContextInitializer.initialize(resourceConfig);

        CookingContextInitializer cookingContextInitializer = new CookingContextInitializer(eventBus, seedFactory);
        cookingContextInitializer.initialize(resourceConfig);

        String everyDayAt9AM = "0 0 9 ? * *";
        SubscriptionContextInitializer subscriptionContextInitializer =
            new SubscriptionContextInitializer(eventBus, paymentService, seedFactory, everyDayAt9AM);
        subscriptionContextInitializer.initialize(resourceConfig);

        DeliveryContextInitializer deliveryContextInitializer = new DeliveryContextInitializer(eventBus, seedFactory);
        deliveryContextInitializer.initialize(resourceConfig);

        LockerAuthorizationContextInitializer lockerAuthorizationContextInitializer = new LockerAuthorizationContextInitializer(eventBus, seedFactory);
        lockerAuthorizationContextInitializer.initialize(resourceConfig);

        // Setup resource config
        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(healthResource).to(HealthResource.class);
            }
        };

        return resourceConfig.packages("ca.ulaval.glo4003.repul").property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true).register(binder)
            .register(new CatchallExceptionMapper()).register(new NotFoundExceptionMapper()).register(new RepULExceptionMapper())
            .register(new ConstraintViolationExceptionMapper());
    }

    @Override
    public String getURI() {
        return String.format("http://localhost:%s/api/", PORT);
    }
}
