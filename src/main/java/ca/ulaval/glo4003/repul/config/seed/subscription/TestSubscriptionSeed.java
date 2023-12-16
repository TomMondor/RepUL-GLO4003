package ca.ulaval.glo4003.repul.config.seed.subscription;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.identitymanagement.api.request.RegistrationRequest;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;
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

public class TestSubscriptionSeed extends SubscriptionSeed {
    public static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    public static final Optional<DeliveryLocationId> OPTIONAL_OF_A_DELIVERY_LOCATION_ID = Optional.of(A_DELIVERY_LOCATION_ID);
    public static final SubscriptionUniqueIdentifier SPORADIC_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("7d6e4bb0-ec23-42c1-928a-2704757436be");
    public static final SubscriptionUniqueIdentifier FIRST_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("7f8ffb13-56c6-4478-86c2-1f1783993e55");
    public static final SubscriptionUniqueIdentifier SECOND_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("4ba3561d-8ef6-4f8c-a7dc-2e9ebfd23597");
    public static final SubscriptionUniqueIdentifier THIRD_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("9d20441d-9d21-4004-a865-3c35800065f2");
    public static final SubscriptionUniqueIdentifier FOURTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("296f5906-1a9b-4488-96a7-a5dbed0c69c3");
    public static final SubscriptionUniqueIdentifier FIFTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("c4168ad3-e10d-4158-9816-a3efc9a2982b");
    public static final SubscriptionUniqueIdentifier SIXTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("a23a0918-76c4-4d8d-b458-2d6cae32d4bd");
    public static final SubscriptionUniqueIdentifier SEVENTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("97d7fe22-5a58-4c67-9704-b042cea9fd18");
    public static final SubscriptionUniqueIdentifier EIGHTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("7a70829c-7e2d-489e-b2e8-6c4f3f9d2b88");
    public static final SubscriptionUniqueIdentifier NINTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("f20c5976-3c5d-490d-b078-877ec3def94c");
    public static final SubscriptionUniqueIdentifier TENTH_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("bf70c9db-c982-4d1a-9619-bf026e10930c");
    public static final MealKitUniqueIdentifier FIRST_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("6be93d65-47ae-44fe-bd4f-a62272a39e37");
    public static final MealKitUniqueIdentifier SECOND_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("39fed158-8b44-4a72-a176-a177012c9c40");
    public static final MealKitUniqueIdentifier THIRD_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("d955a8bc-126e-47a3-965f-7e84167b4d33");
    public static final MealKitUniqueIdentifier FOURTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("efbab5f6-3ec1-4954-8377-98af932060e1");
    public static final MealKitUniqueIdentifier FIFTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("adcabb17-b711-4cf5-a596-f2a029de7582");
    public static final MealKitUniqueIdentifier SIXTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("0285dc6f-d1cc-4bc0-b1ba-5a4ffe46262e");
    public static final MealKitUniqueIdentifier SEVENTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("d61825d4-e270-4c8d-b900-9b9e82fedf7a");
    public static final MealKitUniqueIdentifier EIGHTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("b649b1aa-6e12-4683-a109-2ab148af83d4");
    public static final MealKitUniqueIdentifier TENTH_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("72112714-4a2a-4384-9d1c-6f0e86c7034a");
    public static final MealKitUniqueIdentifier SPORADIC_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("f1e4d64b-ade6-47c7-9f29-1729920e4dc9");
    public static final SubscriberUniqueIdentifier CLIENT_ID =
        new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom("dd79bfb6-33b6-4b14-91e1-d40fec57e0e7");
    public static final String CLIENT_EMAIL = "alexandra@ulaval.ca";
    public static final String CLIENT_PASSWORD = "alexandra123";
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
    private static final Order TENTH_MEAL_KIT_ORDER =
        new Order(TENTH_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Order SPORADIC_ORDER = new Order(SPORADIC_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Optional<WeeklyOccurence> OPTIONAL_OF_A_WEEKLY_FREQUENCY = Optional.of(new WeeklyOccurence(LocalDate.now().getDayOfWeek()));
    private static final Semester A_SEMESTER = new Semester(new SemesterCode("A23"), LocalDate.now(), LocalDate.now().plusWeeks(10));
    private static final RegistrationRequest CLIENT_REGISTRATION_REQUEST =
        new RegistrationRequest("ALEXA123", CLIENT_EMAIL, CLIENT_PASSWORD, "Alexandra", "1999-01-01", "WOMAN");

    public TestSubscriptionSeed(SubscriberRepository subscriberRepository) {
        super(subscriberRepository);
    }

    @Override
    public void populate() {
        Subscriber subscriber = new Subscriber(CLIENT_ID, new IDUL(CLIENT_REGISTRATION_REQUEST.idul), new Name(CLIENT_REGISTRATION_REQUEST.name),
            new Birthdate(LocalDate.parse(CLIENT_REGISTRATION_REQUEST.birthdate)), Gender.from(CLIENT_REGISTRATION_REQUEST.gender),
            new Email(CLIENT_REGISTRATION_REQUEST.email));
        createSubscriptions(subscriber);
        subscriberRepository.save(subscriber);
    }

    private void createSubscriptions(Subscriber subscriber) {
        subscriber.addSubscription(new Subscription(FIRST_SUBSCRIPTION_ID, new Orders(List.of(FIRST_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_FREQUENCY,
            OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD));
        subscriber.addSubscription(new Subscription(SECOND_SUBSCRIPTION_ID, new Orders(List.of(SECOND_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_FREQUENCY,
            OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD));
        subscriber.addSubscription(new Subscription(THIRD_SUBSCRIPTION_ID, new Orders(List.of(THIRD_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_FREQUENCY,
            OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD));
        subscriber.addSubscription(new Subscription(FOURTH_SUBSCRIPTION_ID, new Orders(List.of(FOURTH_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_FREQUENCY,
            OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD));
        subscriber.addSubscription(new Subscription(FIFTH_SUBSCRIPTION_ID, new Orders(List.of(FIFTH_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_FREQUENCY,
            OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD));
        subscriber.addSubscription(new Subscription(SIXTH_SUBSCRIPTION_ID, new Orders(List.of(SIXTH_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_FREQUENCY,
            OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD));
        subscriber.addSubscription(new Subscription(SEVENTH_SUBSCRIPTION_ID, new Orders(List.of(SEVENTH_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_FREQUENCY,
            OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD));
        subscriber.addSubscription(new Subscription(EIGHTH_SUBSCRIPTION_ID, new Orders(List.of(EIGHTH_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_FREQUENCY,
            OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD));
        subscriber.addSubscription(new Subscription(NINTH_SUBSCRIPTION_ID, new Orders(List.of(FOURTH_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_FREQUENCY,
            OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD));
        subscriber.addSubscription(new Subscription(TENTH_SUBSCRIPTION_ID, new Orders(List.of(TENTH_MEAL_KIT_ORDER)), OPTIONAL_OF_A_WEEKLY_FREQUENCY,
            OPTIONAL_OF_A_DELIVERY_LOCATION_ID, LocalDate.now(), A_SEMESTER, MealKitType.STANDARD));
        subscriber.addSubscription(
            new Subscription(SPORADIC_SUBSCRIPTION_ID, new Orders(List.of(SPORADIC_ORDER)), OPTIONAL_OF_A_WEEKLY_FREQUENCY, OPTIONAL_OF_A_DELIVERY_LOCATION_ID,
                LocalDate.now(), A_SEMESTER, MealKitType.STANDARD));
    }
}
