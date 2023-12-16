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
import ca.ulaval.glo4003.repul.config.env.EnvParser;
import ca.ulaval.glo4003.repul.config.env.EnvParserFactory;
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

public class DemoSubscriptionSeed extends SubscriptionSeed {
    public static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    public static final Optional<DeliveryLocationId> OPTIONAL_OF_A_DELIVERY_LOCATION_ID = Optional.of(A_DELIVERY_LOCATION_ID);
    public static final SubscriberUniqueIdentifier CLIENT_ID =
        new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom("dd79bfb6-33b6-4b14-91e1-d40fec57e0e7");
    public static final String CLIENT_PASSWORD = "alexandra123";
    public static final SubscriptionUniqueIdentifier FIRST_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("7f8ffb13-56c6-4478-86c2-1f1783993e55");
    public static final SubscriptionUniqueIdentifier SECOND_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("4ba3561d-8ef6-4f8c-a7dc-2e9ebfd23597");
    public static final SubscriptionUniqueIdentifier THIRD_SUBSCRIPTION_ID =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generateFrom("9d20441d-9d21-4004-a865-3c35800065f2");
    public static final MealKitUniqueIdentifier FIRST_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("6be93d65-47ae-44fe-bd4f-a62272a39e37");
    public static final MealKitUniqueIdentifier SECOND_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("39fed158-8b44-4a72-a176-a177012c9c40");
    public static final MealKitUniqueIdentifier THIRD_MEAL_KIT_ID =
        new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom("d955a8bc-126e-47a3-965f-7e84167b4d33");
    private static final EnvParser ENV_PARSER = EnvParserFactory.getEnvParser(".env");
    private static final String CLIENT_EMAIL =
        ENV_PARSER.readVariable("CLIENT_EMAIL").isBlank() ? "alexandra@ulaval.ca" : ENV_PARSER.readVariable("CLIENT_EMAIL");
    private static final RegistrationRequest CLIENT_REGISTRATION_REQUEST =
        new RegistrationRequest("ALEXA123", CLIENT_EMAIL, CLIENT_PASSWORD, "Alexandra", "1999-01-01", "WOMAN");
    private static final Order FIRST_MEAL_KIT_ORDER =
        new Order(FIRST_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Order SECOND_MEAL_KIT_ORDER =
        new Order(SECOND_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Order THIRD_MEAL_KIT_ORDER =
        new Order(THIRD_MEAL_KIT_ID, MealKitType.STANDARD, LocalDate.now().plusDays(1), OrderStatus.IN_PREPARATION);
    private static final Optional<WeeklyOccurence> OPTIONAL_OF_A_WEEKLY_FREQUENCY = Optional.of(new WeeklyOccurence(LocalDate.now().getDayOfWeek()));
    private static final Semester A_SEMESTER = new Semester(new SemesterCode("A23"), LocalDate.now(), LocalDate.now().plusWeeks(10));

    public DemoSubscriptionSeed(SubscriberRepository subscriberRepository) {
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
    }
}
