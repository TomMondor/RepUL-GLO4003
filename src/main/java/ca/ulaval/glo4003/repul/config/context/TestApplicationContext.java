package ca.ulaval.glo4003.repul.config.context;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import ca.ulaval.glo4003.repul.commons.domain.DateParser;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.MealKitDto;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.CookUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.DeliveryPersonUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.config.Config;
import ca.ulaval.glo4003.repul.config.env.EnvParser;
import ca.ulaval.glo4003.repul.config.initializer.CookingContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.DeliveryContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.IdentityManagementContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.LockerAuthorizationContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.NotificationContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.SubscriptionContextInitializer;
import ca.ulaval.glo4003.repul.config.initializer.jobs.JobInitializer;
import ca.ulaval.glo4003.repul.config.initializer.jobs.ProcessOrdersJobInitializer;
import ca.ulaval.glo4003.repul.cooking.api.MealKitResource;
import ca.ulaval.glo4003.repul.cooking.application.CookingService;
import ca.ulaval.glo4003.repul.cooking.domain.Cook.Cook;
import ca.ulaval.glo4003.repul.delivery.api.CargoResource;
import ca.ulaval.glo4003.repul.delivery.api.LocationResource;
import ca.ulaval.glo4003.repul.delivery.application.DeliveryService;
import ca.ulaval.glo4003.repul.delivery.domain.deliverylocation.DeliveryLocation;
import ca.ulaval.glo4003.repul.health.api.HealthResource;
import ca.ulaval.glo4003.repul.identitymanagement.api.UserResource;
import ca.ulaval.glo4003.repul.identitymanagement.api.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.identitymanagement.middleware.AuthGuard;
import ca.ulaval.glo4003.repul.lockerauthorization.api.LockerAuthorizationResource;
import ca.ulaval.glo4003.repul.lockerauthorization.application.LockerAuthorizationService;
import ca.ulaval.glo4003.repul.lockerauthorization.middleware.ApiKeyGuard;
import ca.ulaval.glo4003.repul.subscription.api.AccountResource;
import ca.ulaval.glo4003.repul.subscription.api.SubscriptionResource;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.WeeklyOccurence;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Orders;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.OrderStatus;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.infrastructure.LogPaymentService;

public class TestApplicationContext implements ApplicationContext {
    public static final SubscriberUniqueIdentifier CLIENT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    public static final String CLIENT_EMAIL = "alexandra@ulaval.ca";
    public static final String CLIENT_PASSWORD = "alexandra123";
    public static final CookUniqueIdentifier COOK_ID = new UniqueIdentifierFactory<>(CookUniqueIdentifier.class).generate();
    public static final String COOK_EMAIL = "paul@ulaval.ca";
    public static final String COOK_PASSWORD = "paul123";
    public static final DeliveryPersonUniqueIdentifier DELIVERY_PERSON_ID = new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    public static final DeliveryPersonUniqueIdentifier SECOND_DELIVERY_PERSON_ID =
        new UniqueIdentifierFactory<>(DeliveryPersonUniqueIdentifier.class).generate();
    public static final String DELIVERY_PERSON_EMAIL = "roger@ulaval.ca";
    public static final String DELIVERY_PERSON_PASSWORD = "roger123";
    public static final String SECOND_DELIVERY_PERSON_EMAIL = "john@ulaval.ca";
    public static final String SECOND_DELIVERY_PERSON_PASSWORD = "john123";
    public static final SubscriptionUniqueIdentifier SPORADIC_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier FIRST_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier SECOND_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier THIRD_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier FOURTH_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier FIFTH_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier SIXTH_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier SEVENTH_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier EIGHTH_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier NINTH_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final SubscriptionUniqueIdentifier TENTH_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier FIRST_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier SECOND_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier THIRD_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier FOURTH_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier FIFTH_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier SIXTH_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier SEVENTH_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier EIGHTH_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier NINTH_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier TENTH_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final MealKitUniqueIdentifier SPORADIC_MEAL_KIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    public static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    public static final Optional<DeliveryLocationId> OPTIONAL_OF_A_DELIVERY_LOCATION_ID = Optional.of(A_DELIVERY_LOCATION_ID);
    public static final DeliveryLocation A_DELIVERY_LOCATION = new DeliveryLocation(A_DELIVERY_LOCATION_ID, "Entrée Vachon #1", 30);
    private static final Order FIRST_MEAL_KIT_ORDER =
        new Order(FIRST_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Order SECOND_MEAL_KIT_ORDER =
        new Order(SECOND_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Order THIRD_MEAL_KIT_ORDER =
        new Order(THIRD_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Order FOURTH_MEAL_KIT_ORDER =
        new Order(FOURTH_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Order FIFTH_MEAL_KIT_ORDER =
        new Order(FIFTH_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Order SIXTH_MEAL_KIT_ORDER =
        new Order(SIXTH_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Order SEVENTH_MEAL_KIT_ORDER =
        new Order(SEVENTH_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Order EIGHTH_MEAL_KIT_ORDER =
        new Order(EIGHTH_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Order NINTH_MEAL_KIT_ORDER =
        new Order(NINTH_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Order TENTH_MEAL_KIT_ORDER =
        new Order(TENTH_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Order SPORADIC_ORDER = new Order(SPORADIC_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Optional<WeeklyOccurence> OPTIONAL_OF_A_WEEKLY_OCCURENCE = Optional.of(new WeeklyOccurence(LocalDate.now().getDayOfWeek()));
    private static final Semester A_SEMESTER = new Semester(new SemesterCode("A23"), LocalDate.now(), LocalDate.now().plusWeeks(10));
    private static final RegistrationRequest CLIENT_REGISTRATION_REQUEST =
        new RegistrationRequest("ALEXA123", CLIENT_EMAIL, CLIENT_PASSWORD, "Alexandra", "1999-01-01", "WOMAN");
    private static final Subscriber SUBSCRIBER =
        new Subscriber(CLIENT_ID, new IDUL(CLIENT_REGISTRATION_REQUEST.idul), new Name(CLIENT_REGISTRATION_REQUEST.name),
            new Birthdate(DateParser.localDateFrom(CLIENT_REGISTRATION_REQUEST.birthdate)), Gender.from(CLIENT_REGISTRATION_REQUEST.gender),
            new Email(CLIENT_REGISTRATION_REQUEST.email));
    private static final RegistrationRequest COOK_REGISTRATION_REQUEST =
        new RegistrationRequest("PAUL123", COOK_EMAIL, COOK_PASSWORD, "Paul", "1990-01-01", "MAN");
    private static final RegistrationRequest DELIVERY_PERSON_REGISTRATION_REQUEST =
        new RegistrationRequest("ROGER456", DELIVERY_PERSON_EMAIL, DELIVERY_PERSON_PASSWORD, "Roger", "1973-04-24", "MAN");
    private static final RegistrationRequest SECOND_DELIVERY_PERSON_REGISTRATION_REQUEST =
        new RegistrationRequest("JOHN456", SECOND_DELIVERY_PERSON_EMAIL, SECOND_DELIVERY_PERSON_PASSWORD, "John", "1973-05-24", "MAN");
    private static final Logger LOGGER = LoggerFactory.getLogger(TestApplicationContext.class);
    private static final int PORT = 8081;
    private static Cook cook = new Cook(COOK_ID);

    public TestApplicationContext() {
        LocalTime openingTime = LocalTime.of(9, 0);
        Duration durationToConfirm = Duration.ofDays(2);
        Config.initialize(openingTime, durationToConfirm);
    }

    @Override
    public ResourceConfig initializeResourceConfig() {
        EnvParser.setFilename(".envExample");
        RepULEventBus eventBus = new GuavaEventBus();

        PaymentService paymentService = new LogPaymentService();

        LOGGER.info("Creating Health resource");
        HealthResource healthResource = new HealthResource();

        NotificationContextInitializer notificationContextInitializer = new NotificationContextInitializer().withDeliveryAccounts(
                List.of(Map.of(DELIVERY_PERSON_ID, DELIVERY_PERSON_EMAIL), Map.of(SECOND_DELIVERY_PERSON_ID, SECOND_DELIVERY_PERSON_EMAIL)))
            .withUserAccounts(List.of(Map.of(CLIENT_ID, CLIENT_EMAIL))).withMealKitIdForUser(FIRST_MEAL_KIT_ID, CLIENT_ID)
            .withMealKitIdForUser(TENTH_MEAL_KIT_ID, CLIENT_ID);
        notificationContextInitializer.createNotificationService(eventBus);

        IdentityManagementContextInitializer identityManagementContextInitializer =
            new IdentityManagementContextInitializer(eventBus).withClients(List.of(Map.of(CLIENT_ID, CLIENT_REGISTRATION_REQUEST)))
                .withCooks(List.of(Map.of(COOK_ID, COOK_REGISTRATION_REQUEST))).withShippers(
                    List.of(Map.of(DELIVERY_PERSON_ID, DELIVERY_PERSON_REGISTRATION_REQUEST),
                        Map.of(SECOND_DELIVERY_PERSON_ID, SECOND_DELIVERY_PERSON_REGISTRATION_REQUEST)));

        UserResource userResource = new UserResource(identityManagementContextInitializer.createService());
        AuthGuard authGuard = identityManagementContextInitializer.createAuthGuard();

        cook = new Cook(COOK_ID);
        CookingContextInitializer cookingContextInitializer = new CookingContextInitializer()
            .withMealKitsForSubscription(List.of(SPORADIC_ORDER), SPORADIC_SUBSCRIPTION_ID, CLIENT_ID, Optional.empty())
            .withMealKitsForSubscription(List.of(FIRST_MEAL_KIT_ORDER), FIRST_SUBSCRIPTION_ID, CLIENT_ID, Optional.of(DeliveryLocationId.VACHON))
            .withMealKitsForSubscription(List.of(SECOND_MEAL_KIT_ORDER), SECOND_SUBSCRIPTION_ID, CLIENT_ID, Optional.of(DeliveryLocationId.VACHON))
            .withMealKitsForSubscription(List.of(THIRD_MEAL_KIT_ORDER), THIRD_SUBSCRIPTION_ID, CLIENT_ID, Optional.of(DeliveryLocationId.VACHON))
            .withMealKitsForSubscription(List.of(FOURTH_MEAL_KIT_ORDER), FOURTH_SUBSCRIPTION_ID, CLIENT_ID, Optional.of(DeliveryLocationId.VACHON))
            .withMealKitsForSubscription(List.of(FIFTH_MEAL_KIT_ORDER), FIFTH_SUBSCRIPTION_ID, CLIENT_ID, Optional.of(DeliveryLocationId.VACHON))
            .withMealKitsForSubscription(List.of(SIXTH_MEAL_KIT_ORDER), SIXTH_SUBSCRIPTION_ID, CLIENT_ID, Optional.of(DeliveryLocationId.VACHON))
            .withMealKitsForSubscription(List.of(SEVENTH_MEAL_KIT_ORDER), SEVENTH_SUBSCRIPTION_ID, CLIENT_ID, Optional.of(DeliveryLocationId.VACHON))
            .withMealKitsForSubscription(List.of(EIGHTH_MEAL_KIT_ORDER), EIGHTH_SUBSCRIPTION_ID, CLIENT_ID, Optional.of(DeliveryLocationId.VACHON))
            .withMealKitsForSubscription(List.of(NINTH_MEAL_KIT_ORDER), NINTH_SUBSCRIPTION_ID, CLIENT_ID, Optional.of(DeliveryLocationId.VACHON))
            .withMealKitsForSubscription(List.of(TENTH_MEAL_KIT_ORDER), TENTH_SUBSCRIPTION_ID, CLIENT_ID, Optional.of(DeliveryLocationId.VACHON))
            .withCook(cook);
        CookingService cookingService = cookingContextInitializer.createCookingService(eventBus);
        MealKitResource mealKitResource = new MealKitResource(cookingService);
        cookingContextInitializer.createMealKitEventHandler(cookingService, eventBus);

        SubscriptionContextInitializer subscriptionContextInitializer = new SubscriptionContextInitializer().withSubscribers(List.of(SUBSCRIBER))
            .withSubscriptionsForSubscriber(List.of(
                new Subscription(FIRST_SUBSCRIPTION_ID, new Orders(List.of(FIRST_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_OCCURENCE,
                    OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD),
                new Subscription(SECOND_SUBSCRIPTION_ID, new Orders(List.of(SECOND_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_OCCURENCE,
                    OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD),
                new Subscription(THIRD_SUBSCRIPTION_ID, new Orders(List.of(THIRD_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_OCCURENCE,
                    OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD),
                new Subscription(FOURTH_SUBSCRIPTION_ID, new Orders(List.of(FOURTH_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_OCCURENCE,
                    OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD),
                new Subscription(FIFTH_SUBSCRIPTION_ID, new Orders(List.of(FIFTH_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_OCCURENCE,
                    OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD),
                new Subscription(SIXTH_SUBSCRIPTION_ID, new Orders(List.of(SIXTH_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_OCCURENCE,
                    OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD),
                new Subscription(SEVENTH_SUBSCRIPTION_ID, new Orders(List.of(SEVENTH_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_OCCURENCE,
                    OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD),
                new Subscription(EIGHTH_SUBSCRIPTION_ID, new Orders(List.of(EIGHTH_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_OCCURENCE,
                    OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD),
                new Subscription(EIGHTH_SUBSCRIPTION_ID, new Orders(List.of(EIGHTH_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_OCCURENCE,
                    OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD),
                new Subscription(SPORADIC_SUBSCRIPTION_ID, new Orders(List.of(SPORADIC_ORDER)), OPTIONAL_OF_A_WEEKLY_OCCURENCE,
                    OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD),
                new Subscription(NINTH_SUBSCRIPTION_ID, new Orders(List.of(SPORADIC_ORDER)), OPTIONAL_OF_A_WEEKLY_OCCURENCE, OPTIONAL_OF_A_DELIVERY_LOCATION_ID,
                    LocalDate.now(), A_SEMESTER, MealKitType.STANDARD),
                new Subscription(TENTH_SUBSCRIPTION_ID, new Orders(List.of(TENTH_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_OCCURENCE,
                    OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD)), CLIENT_ID);
        SubscriberService subscriberService = subscriptionContextInitializer.createSubscriberService(eventBus, paymentService);
        AccountResource accountResource = new AccountResource(subscriberService);
        SubscriptionResource subscriptionResource = new SubscriptionResource(subscriberService);

        subscriptionContextInitializer.createSubscriberEventHandler(subscriberService, eventBus);

        DeliveryContextInitializer deliveryContextInitializer = new DeliveryContextInitializer()
            .withDeliveryPeople(List.of(DELIVERY_PERSON_ID, SECOND_DELIVERY_PERSON_ID))
            .withPendingMealKits(List.of(
                Map.of(new MealKitDto(SUBSCRIBER.getSubscriberId(), FIRST_SUBSCRIPTION_ID, FIRST_MEAL_KIT_ID), A_DELIVERY_LOCATION_ID),
                Map.of(new MealKitDto(SUBSCRIBER.getSubscriberId(), SECOND_SUBSCRIPTION_ID, SECOND_MEAL_KIT_ID), A_DELIVERY_LOCATION_ID),
                Map.of(new MealKitDto(SUBSCRIBER.getSubscriberId(), THIRD_SUBSCRIPTION_ID, THIRD_MEAL_KIT_ID), A_DELIVERY_LOCATION_ID),
                Map.of(new MealKitDto(SUBSCRIBER.getSubscriberId(), FOURTH_SUBSCRIPTION_ID, FOURTH_MEAL_KIT_ID), A_DELIVERY_LOCATION_ID),
                Map.of(new MealKitDto(SUBSCRIBER.getSubscriberId(), FIFTH_SUBSCRIPTION_ID, FIFTH_MEAL_KIT_ID), A_DELIVERY_LOCATION_ID),
                Map.of(new MealKitDto(SUBSCRIBER.getSubscriberId(), SIXTH_SUBSCRIPTION_ID, SIXTH_MEAL_KIT_ID), A_DELIVERY_LOCATION_ID),
                Map.of(new MealKitDto(SUBSCRIBER.getSubscriberId(), SEVENTH_SUBSCRIPTION_ID, SEVENTH_MEAL_KIT_ID), A_DELIVERY_LOCATION_ID),
                Map.of(new MealKitDto(SUBSCRIBER.getSubscriberId(), EIGHTH_SUBSCRIPTION_ID, EIGHTH_MEAL_KIT_ID), A_DELIVERY_LOCATION_ID),
                Map.of(new MealKitDto(SUBSCRIBER.getSubscriberId(), NINTH_SUBSCRIPTION_ID, NINTH_MEAL_KIT_ID), A_DELIVERY_LOCATION_ID),
                Map.of(new MealKitDto(SUBSCRIBER.getSubscriberId(), TENTH_SUBSCRIPTION_ID, TENTH_MEAL_KIT_ID), A_DELIVERY_LOCATION_ID)))
            .withCargo(List.of(TENTH_MEAL_KIT_ID));
        DeliveryService deliveryService = deliveryContextInitializer.createDeliveryService(eventBus);
        CargoResource cargoResource = new CargoResource(deliveryService);
        LocationResource locationResource = new LocationResource(deliveryContextInitializer.createLocationsCatalogService());
        deliveryContextInitializer.createDeliveryEventHandler(deliveryService, eventBus);

        LockerAuthorizationContextInitializer lockerAuthorizationContextInitializer = new LockerAuthorizationContextInitializer().withOrders(
            List.of(
                Map.entry(CLIENT_ID, new MealKitDto(CLIENT_ID, FIRST_SUBSCRIPTION_ID, FIRST_MEAL_KIT_ID)),
                Map.entry(CLIENT_ID, new MealKitDto(CLIENT_ID, SECOND_SUBSCRIPTION_ID, SECOND_MEAL_KIT_ID)),
                Map.entry(CLIENT_ID, new MealKitDto(CLIENT_ID, THIRD_SUBSCRIPTION_ID, THIRD_MEAL_KIT_ID)),
                Map.entry(CLIENT_ID, new MealKitDto(CLIENT_ID, FOURTH_SUBSCRIPTION_ID, FOURTH_MEAL_KIT_ID)),
                Map.entry(CLIENT_ID, new MealKitDto(CLIENT_ID, FIFTH_SUBSCRIPTION_ID, FIFTH_MEAL_KIT_ID)),
                Map.entry(CLIENT_ID, new MealKitDto(CLIENT_ID, SIXTH_SUBSCRIPTION_ID, SIXTH_MEAL_KIT_ID)),
                Map.entry(CLIENT_ID, new MealKitDto(CLIENT_ID, SEVENTH_SUBSCRIPTION_ID, SEVENTH_MEAL_KIT_ID)),
                Map.entry(CLIENT_ID, new MealKitDto(CLIENT_ID, EIGHTH_SUBSCRIPTION_ID, EIGHTH_MEAL_KIT_ID)),
                Map.entry(CLIENT_ID, new MealKitDto(CLIENT_ID, TENTH_SUBSCRIPTION_ID, TENTH_MEAL_KIT_ID))
            ));
        LockerAuthorizationService lockerAuthorizationService = lockerAuthorizationContextInitializer.createLockerAuthorizationService(eventBus);
        LockerAuthorizationResource lockerAuthorizationResource = new LockerAuthorizationResource(lockerAuthorizationService);
        lockerAuthorizationContextInitializer.createLockerAuthorizationEventHandler(lockerAuthorizationService, eventBus);
        ApiKeyGuard apiKeyGuard = lockerAuthorizationContextInitializer.createApiKeyGuard();

        String everyDayAt9AM = "0 0 9 ? * *";
        JobInitializer processOrdersJob = new ProcessOrdersJobInitializer(subscriberService, everyDayAt9AM);
        processOrdersJob.launchJob();

        // Setup resource config
        final AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {
                bind(healthResource).to(HealthResource.class);
                bind(userResource).to(UserResource.class);
                bind(accountResource).to(AccountResource.class);
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
