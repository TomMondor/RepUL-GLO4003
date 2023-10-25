package ca.ulaval.glo4003.repul.config;

import java.time.LocalDate;
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
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.config.http.CORSResponseFilter;
import ca.ulaval.glo4003.repul.config.initializer.CookingContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.DeliveryContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.NotificationContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.SubscriptionContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.UserContextInitializer;
import ca.ulaval.glo4003.repul.cooking.api.MealKitResource;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.delivery.api.DeliveryResource;
import ca.ulaval.glo4003.repul.delivery.api.LocationsCatalogResource;
import ca.ulaval.glo4003.repul.health.api.HealthResource;
import ca.ulaval.glo4003.repul.payment.application.PaymentService;
import ca.ulaval.glo4003.repul.subscription.api.SubscriptionResource;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.user.api.UserResource;
import ca.ulaval.glo4003.repul.user.application.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.user.middleware.AuthGuard;

public class TestApplicationContext implements ApplicationContext {
    public static final UniqueIdentifier CLIENT_ID = new UniqueIdentifier(UUID.randomUUID());
    public static final String CLIENT_EMAIL = "alexandra@ulaval.ca";
    public static final String CLIENT_PASSWORD = "alexandra123";
    public static final UniqueIdentifier COOK_ID = new UniqueIdentifier(UUID.randomUUID());
    public static final String COOK_EMAIL = "paul@ulaval.ca";
    public static final String COOK_PASSWORD = "paul123";
    public static final UniqueIdentifier SHIPPER_ID = new UniqueIdentifier(UUID.randomUUID());
    public static final String SHIPPER_EMAIL = "roger@ulaval.ca";
    private static final RegistrationQuery CLIENT_REGISTRATION_QUERY =
        RegistrationQuery.from(CLIENT_EMAIL, CLIENT_PASSWORD, "ALEXA123", "Alexandra", "1999-01-01", "WOMAN");
    private static final RegistrationQuery COOK_REGISTRATION_QUERY = RegistrationQuery.from(COOK_EMAIL, COOK_PASSWORD, "PAUL123", "Paul", "1990-01-01", "MAN");
    private static final RegistrationQuery SHIPPER_REGISTRATION_QUERY =
        RegistrationQuery.from(SHIPPER_EMAIL, "roger123", "ROGER456", "Roger", "1973-04-24", "MAN");
    private static final Logger LOGGER = LoggerFactory.getLogger(TestApplicationContext.class);
    private static final int PORT = 8081;

    @Override
    public ResourceConfig initializeResourceConfig() {
        RepULEventBus eventBus = new GuavaEventBus();

        LOGGER.info("Creating Health resource");
        HealthResource healthResource = new HealthResource();

        NotificationContextInitializer notificationContextInitializer =
            new NotificationContextInitializer().withAccounts(List.of(Map.of(SHIPPER_ID, SHIPPER_EMAIL)));
        notificationContextInitializer.createNotificationService(eventBus);

        PaymentService paymentService = new PaymentService();
        eventBus.register(paymentService);

        UserContextInitializer userContextInitializer = new UserContextInitializer(eventBus).withClients(List.of(Map.of(CLIENT_ID, CLIENT_REGISTRATION_QUERY)))
            .withCooks(List.of(Map.of(COOK_ID, COOK_REGISTRATION_QUERY))).withShippers(List.of(Map.of(SHIPPER_ID, SHIPPER_REGISTRATION_QUERY)));

        UserResource userResource = new UserResource(userContextInitializer.createService());
        AuthGuard authGuard = userContextInitializer.createAuthGuard();

        CookingContextInitializer cookingContextInitializer = new CookingContextInitializer().withMealKits(
            List.of(new Order(new UniqueIdentifier(UUID.randomUUID()), MealKitType.STANDARD, LocalDate.now().plusDays(1))));
        CookingService cookingService = cookingContextInitializer.createCookingService(eventBus);
        MealKitResource mealKitResource = new MealKitResource(cookingService);

        SubscriptionContextInitializer subscriptionContextInitializer = new SubscriptionContextInitializer();
        SubscriptionResource subscriptionResource = new SubscriptionResource(subscriptionContextInitializer.createSubscriptionService(eventBus));

        DeliveryContextInitializer deliveryContextInitializer = new DeliveryContextInitializer().withDeliveryPeople(List.of(SHIPPER_ID));
        DeliveryResource deliveryResource = new DeliveryResource(deliveryContextInitializer.createDeliveryService(eventBus));
        LocationsCatalogResource locationsCatalogResource = new LocationsCatalogResource(deliveryContextInitializer.createLocationsCatalogService());

        // Setup resource config
        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(healthResource).to(HealthResource.class);
                bind(userResource).to(UserResource.class);
                bind(mealKitResource).to(MealKitResource.class);
                bind(deliveryResource).to(DeliveryResource.class);
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
