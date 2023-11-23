package ca.ulaval.glo4003.repul.config.context;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.config.env.EnvParser;
import ca.ulaval.glo4003.repul.config.env.EnvParserFactory;
import ca.ulaval.glo4003.repul.config.initializer.CookingContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.DeliveryContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.LockerAuthorizationContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.NotificationContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.SubscriptionContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.UserContextInitializer;
import ca.ulaval.glo4003.repul.cooking.api.MealKitResource;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.delivery.api.CargoResource;
import ca.ulaval.glo4003.repul.delivery.api.LocationResource;
import ca.ulaval.glo4003.repul.health.api.HealthResource;
import ca.ulaval.glo4003.repul.lockerauthorization.api.LockerAuthorizationResource;
import ca.ulaval.glo4003.repul.lockerauthorization.middleware.ApiKeyGuard;
import ca.ulaval.glo4003.repul.notification.infrastructure.EmailNotificationSender;
import ca.ulaval.glo4003.repul.payment.application.PaymentService;
import ca.ulaval.glo4003.repul.subscription.api.SubscriptionResource;
import ca.ulaval.glo4003.repul.subscription.domain.Frequency;
import ca.ulaval.glo4003.repul.subscription.domain.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrderStatus;
import ca.ulaval.glo4003.repul.user.api.UserResource;
import ca.ulaval.glo4003.repul.user.application.query.RegistrationQuery;
import ca.ulaval.glo4003.repul.user.middleware.AuthGuard;

public class DemoApplicationContext implements ApplicationContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoApplicationContext.class);
    private static final EnvParser ENV_PARSER = EnvParserFactory.getEnvParser(".env");
    private static final UniqueIdentifier DELIVERY_PERSON_ID = new UniqueIdentifierFactory().generate();
    private static final UniqueIdentifier COOK_ID = new UniqueIdentifierFactory().generate();
    private static final RegistrationQuery COOK_REGISTRATION_QUERY =
        RegistrationQuery.from("paul@ulaval.ca", "paul123", "PAUL123", "Paul", "1990-01-01", "MAN");
    private static final String CLIENT_EMAIL =
        ENV_PARSER.readVariable("CLIENT_EMAIL").isBlank() ? "alexandra@ulaval.ca" : ENV_PARSER.readVariable("CLIENT_EMAIL");
    private static final RegistrationQuery CLIENT_REGISTRATION_QUERY =
        RegistrationQuery.from(CLIENT_EMAIL, "alexandra123", "alexa228", "Alexandra", "1999-06-08", "WOMAN");
    private static final UniqueIdentifier CLIENT_ID = new UniqueIdentifierFactory().generate();
    private static final Order FIRST_MEAL_KIT_ORDER =
        new Order(new UniqueIdentifierFactory().generate(), MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.TO_COOK);
    private static final Order SECOND_MEAL_KIT_ORDER =
        new Order(new UniqueIdentifierFactory().generate(), MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.TO_COOK);
    private static final Order THIRD_MEAL_KIT_ORDER =
        new Order(new UniqueIdentifierFactory().generate(), MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.TO_COOK);
    private static final Frequency A_WEEKLY_FREQUENCY = new Frequency(LocalDate.now().getDayOfWeek().plus(1));
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId("VACHON");
    private static final Semester A_SEMESTER = new Semester(new SemesterCode("A23"), LocalDate.parse("2023-09-04"), LocalDate.parse("2023-12-15"));
    private static final Subscription FIRST_SUBSCRIPTION =
        new Subscription(new UniqueIdentifierFactory().generate(), CLIENT_ID, List.of(FIRST_MEAL_KIT_ORDER), A_WEEKLY_FREQUENCY, A_DELIVERY_LOCATION_ID,
            LocalDate.now(), A_SEMESTER, MealKitType.STANDARD);
    private static final Subscription SECOND_SUBSCRIPTION =
        new Subscription(new UniqueIdentifierFactory().generate(), CLIENT_ID, List.of(SECOND_MEAL_KIT_ORDER), A_WEEKLY_FREQUENCY, A_DELIVERY_LOCATION_ID,
            LocalDate.now(), A_SEMESTER, MealKitType.STANDARD);
    private static final Subscription THIRD_SUBSCRIPTION =
        new Subscription(new UniqueIdentifierFactory().generate(), CLIENT_ID, List.of(THIRD_MEAL_KIT_ORDER), A_WEEKLY_FREQUENCY, A_DELIVERY_LOCATION_ID,
            LocalDate.now(), A_SEMESTER, MealKitType.STANDARD);
    private static final int PORT = 8080;
    private static final String DELIVERY_PERSON_EMAIL =
        ENV_PARSER.readVariable("DELIVERY_PERSON_EMAIL").isBlank() ? "roger@ulaval.ca" : ENV_PARSER.readVariable("DELIVERY_PERSON_EMAIL");
    private static final RegistrationQuery DELIVERY_PERSON_REGISTRATION_EMAIL =
        RegistrationQuery.from(DELIVERY_PERSON_EMAIL, "roger123", "ROGER456", "Roger", "1973-04-24", "MAN");

    @Override
    public ResourceConfig initializeResourceConfig() {
        RepULEventBus eventBus = new GuavaEventBus();

        LOGGER.info("Creating Health resource");
        HealthResource healthResource = new HealthResource();

        NotificationContextInitializer notificationContextInitializer =
            new NotificationContextInitializer().withNotificationSender(new EmailNotificationSender())
                .withDeliveryAccounts(List.of(Map.of(DELIVERY_PERSON_ID, DELIVERY_PERSON_EMAIL))).withUserAccounts(List.of(Map.of(CLIENT_ID, CLIENT_EMAIL)))
                .withConfirmedSubscriptions(List.of(FIRST_SUBSCRIPTION, SECOND_SUBSCRIPTION, THIRD_SUBSCRIPTION));
        notificationContextInitializer.createNotificationService(eventBus);

        PaymentService paymentService = new PaymentService();
        eventBus.register(paymentService);

        UserContextInitializer userContextInitializer = new UserContextInitializer(eventBus).withCooks(List.of(Map.of(COOK_ID, COOK_REGISTRATION_QUERY)))
            .withShippers(List.of(Map.of(DELIVERY_PERSON_ID, DELIVERY_PERSON_REGISTRATION_EMAIL)))
            .withClients(List.of(Map.of(CLIENT_ID, CLIENT_REGISTRATION_QUERY)));

        UserResource userResource = new UserResource(userContextInitializer.createService());
        AuthGuard authGuard = userContextInitializer.createAuthGuard();

        CookingContextInitializer cookingContextInitializer =
            new CookingContextInitializer().withMealKits(List.of(FIRST_MEAL_KIT_ORDER, SECOND_MEAL_KIT_ORDER, THIRD_MEAL_KIT_ORDER));
        CookingService cookingService = cookingContextInitializer.createCookingService(eventBus);
        MealKitResource mealKitResource = new MealKitResource(cookingService);

        SubscriptionContextInitializer subscriptionContextInitializer =
            new SubscriptionContextInitializer().withSubscriptions(List.of(FIRST_SUBSCRIPTION, SECOND_SUBSCRIPTION, THIRD_SUBSCRIPTION));
        SubscriptionResource subscriptionResource = new SubscriptionResource(subscriptionContextInitializer.createSubscriptionService(eventBus));

        DeliveryContextInitializer deliveryContextInitializer = new DeliveryContextInitializer().withDeliveryPeople(List.of(DELIVERY_PERSON_ID))
            .withPendingMealKits(
                List.of(Map.of(FIRST_MEAL_KIT_ORDER.getOrderId(), A_DELIVERY_LOCATION_ID), Map.of(SECOND_MEAL_KIT_ORDER.getOrderId(), A_DELIVERY_LOCATION_ID),
                    Map.of(THIRD_MEAL_KIT_ORDER.getOrderId(), A_DELIVERY_LOCATION_ID)));
        CargoResource cargoResource = new CargoResource(deliveryContextInitializer.createDeliveryService(eventBus));
        LocationResource locationResource = new LocationResource(deliveryContextInitializer.createLocationsCatalogService());

        LockerAuthorizationContextInitializer lockerAuthorizationContextInitializer = new LockerAuthorizationContextInitializer().withOrders(
            List.of(Map.entry(CLIENT_ID, FIRST_MEAL_KIT_ORDER.getOrderId()),
                Map.entry(CLIENT_ID, SECOND_MEAL_KIT_ORDER.getOrderId()),
                Map.entry(CLIENT_ID, THIRD_MEAL_KIT_ORDER.getOrderId())));
        LockerAuthorizationResource lockerAuthorizationResource =
            new LockerAuthorizationResource(lockerAuthorizationContextInitializer.createLockerAuthorizationService(eventBus));
        ApiKeyGuard apiKeyGuard = lockerAuthorizationContextInitializer.createApiKeyGuard();

        // Setup resource config
        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(healthResource).to(HealthResource.class);
                bind(userResource).to(UserResource.class);
                bind(mealKitResource).to(MealKitResource.class);
                bind(cargoResource).to(CargoResource.class);
                bind(locationResource).to(LocationResource.class);
                bind(subscriptionResource).to(SubscriptionResource.class);
                bind(lockerAuthorizationResource).to(LockerAuthorizationResource.class);
            }
        };

        return new ResourceConfig().packages("ca.ulaval.glo4003.repul").property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true).register(binder)
            .register(authGuard).register(apiKeyGuard).register(new CatchallExceptionMapper()).register(new NotFoundExceptionMapper())
            .register(new RepULExceptionMapper()).register(new ConstraintViolationExceptionMapper());
    }

    @Override
    public String getURI() {
        return String.format("http://localhost:%s/api/", PORT);
    }
}
