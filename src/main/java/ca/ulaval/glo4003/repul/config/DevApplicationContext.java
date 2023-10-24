package ca.ulaval.glo4003.repul.config;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ulaval.glo4003.repul.commons.api.exception.mapper.CatchallExceptionMapper;
import ca.ulaval.glo4003.repul.commons.api.exception.mapper.ConstraintViolationExceptionMapper;
import ca.ulaval.glo4003.repul.commons.api.exception.mapper.RepULExceptionMapper;
import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.config.http.CORSResponseFilter;
import ca.ulaval.glo4003.repul.config.initializer.CookingContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.NotificationContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.ShippingContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.SubscriptionContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.UserContextInitializer;
import ca.ulaval.glo4003.repul.cooking.api.MealKitResource;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.health.api.HealthResource;
import ca.ulaval.glo4003.repul.notification.infrastructure.EmailNotificationSender;
import ca.ulaval.glo4003.repul.payment.application.PaymentService;
import ca.ulaval.glo4003.repul.shipping.api.LocationsCatalogResource;
import ca.ulaval.glo4003.repul.shipping.api.ShippingResource;
import ca.ulaval.glo4003.repul.subscription.api.SubscriptionResource;
import ca.ulaval.glo4003.repul.user.api.UserResource;
import ca.ulaval.glo4003.repul.user.application.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.user.middleware.AuthGuard;

public class DevApplicationContext implements ApplicationContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(DevApplicationContext.class);
    private static final int PORT = 8080;
    private static final UniqueIdentifier COOK_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final RegistrationQuery COOK_REGISTRATION_QUERY =
        RegistrationQuery.from("paul@ulaval.ca", "paul123", "PAUL123", "Paul", "1990-01-01", "MAN");
    private static final UniqueIdentifier SHIPPER_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final String SHIPPER_EMAIL = "roger@ulaval.ca";
    private static final RegistrationQuery SHIPPER_REGISTRATION_QUERY =
        RegistrationQuery.from(SHIPPER_EMAIL, "roger123", "ROGER456", "Roger", "1973-04-24", "MAN");

    @Override
    public ResourceConfig initializeResourceConfig() {
        RepULEventBus eventBus = new GuavaEventBus();

        LOGGER.info("Creating Health resource");
        HealthResource healthResource = new HealthResource();

        NotificationContextInitializer notificationContextInitializer =
            new NotificationContextInitializer().withNotificationSender(new EmailNotificationSender()).withAccounts(List.of(Map.of(SHIPPER_ID, SHIPPER_EMAIL)));
        notificationContextInitializer.createNotificationService(eventBus);

        PaymentService paymentService = new PaymentService();
        eventBus.register(paymentService);

        UserContextInitializer userContextInitializer = new UserContextInitializer(eventBus).withCooks(List.of(Map.of(COOK_ID, COOK_REGISTRATION_QUERY)))
            .withShippers(List.of(Map.of(SHIPPER_ID, SHIPPER_REGISTRATION_QUERY)));

        UserResource userResource = new UserResource(userContextInitializer.createService());
        AuthGuard authGuard = userContextInitializer.createAuthGuard();

        CookingContextInitializer cookingContextInitializer = new CookingContextInitializer();
        CookingService cookingService = cookingContextInitializer.createCookingService(eventBus);
        MealKitResource mealKitResource = new MealKitResource(cookingService);

        SubscriptionContextInitializer subscriptionContextInitializer = new SubscriptionContextInitializer();
        SubscriptionResource subscriptionResource = new SubscriptionResource(subscriptionContextInitializer.createSubscriptionService(eventBus));

        ShippingContextInitializer shippingContextInitializer = new ShippingContextInitializer().withShippers(List.of(SHIPPER_ID));
        ShippingResource shippingResource = new ShippingResource(shippingContextInitializer.createShippingService(eventBus));
        LocationsCatalogResource locationsCatalogResource = new LocationsCatalogResource(shippingContextInitializer.createLocationsCatalogService());

        // Setup resource config
        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(healthResource).to(HealthResource.class);
                bind(userResource).to(UserResource.class);
                bind(mealKitResource).to(MealKitResource.class);
                bind(shippingResource).to(ShippingResource.class);
                bind(locationsCatalogResource).to(LocationsCatalogResource.class);
                bind(subscriptionResource).to(SubscriptionResource.class);
            }
        };

        return new ResourceConfig().packages("ca.ulaval.glo4003.repul").property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true).register(binder)
            .register(authGuard).register(new CORSResponseFilter()).register(new CatchallExceptionMapper()).register(new RepULExceptionMapper())
            .register(new ConstraintViolationExceptionMapper());
    }

    @Override
    public String getURI() {
        return String.format("http://localhost:%s/api/", PORT);
    }
}
