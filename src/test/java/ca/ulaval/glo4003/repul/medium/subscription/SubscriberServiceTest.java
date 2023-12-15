package ca.ulaval.glo4003.repul.medium.subscription;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.SubscriberCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.ProfilePayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionType;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.subscription.domain.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.OrdersFactory;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.OrderStatus;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.infrastructure.LogPaymentService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriberServiceTest {
    private static final DeliveryLocationId A_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final DeliveryLocationId ANOTHER_LOCATION_ID = DeliveryLocationId.PEPS;
    private static final Semester CURRENT_SEMESTER = new Semester(new SemesterCode("A23"), LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2));
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final IDUL AN_IDUL = new IDUL("ALMAT69");
    private static final Name A_NAME = new Name("John Doe");
    private static final Birthdate A_BIRTHDATE = new Birthdate(LocalDate.parse("1969-04-20"));
    private static final Gender A_GENDER = Gender.OTHER;
    private static final Email AN_EMAIL = new Email("anEmail@ulaval.ca");
    private static final SubscriberCardNumber A_CARD_NUMBER = new SubscriberCardNumber("123456789");
    private static final DayOfWeek A_DAY_OF_WEEK = DayOfWeek.from(LocalDate.now().plusDays(3));
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final SubscriptionType A_WEEKLY_SUBSCRIPTION_TYPE = SubscriptionType.WEEKLY;
    private static final SubscriptionType A_SPORADIC_SUBSCRIPTION_TYPE = SubscriptionType.SPORADIC;

    private SubscriberService subscriberService;

    @BeforeEach
    public void createSubscriberService() {
        SubscriberRepository subscriberRepository = new InMemorySubscriberRepository();
        UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class);
        OrdersFactory ordersFactory = new OrdersFactory(mealKitUniqueIdentifierFactory);
        SubscriptionFactory subscriptionFactory =
            new SubscriptionFactory(new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class), ordersFactory,
                List.of(CURRENT_SEMESTER), List.of(A_LOCATION_ID, ANOTHER_LOCATION_ID));
        subscriberService =
            new SubscriberService(subscriberRepository, new SubscriberFactory(), subscriptionFactory, new GuavaEventBus(), new LogPaymentService(),
                ordersFactory);
    }

    @Test
    public void whenCreatingSubscriber_shouldPersistSubscriber() {
        subscriberService.createSubscriber(A_SUBSCRIBER_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);

        ProfilePayload retrievedProfile = subscriberService.getSubscriberProfile(A_SUBSCRIBER_ID);
        assertEquals(AN_IDUL.value(), retrievedProfile.idul());
    }

    @Test
    public void givenSubscriberWithSubscriptions_whenGettingAllSubscriptions_shouldReturnSubscriptionsInAccount() {
        subscriberService.createSubscriber(A_SUBSCRIBER_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);
        SubscriptionUniqueIdentifier subscriptionId = subscriberService.createSubscription(
            new SubscriptionQuery(A_WEEKLY_SUBSCRIPTION_TYPE, A_SUBSCRIBER_ID, Optional.of(A_LOCATION_ID), Optional.of(A_DAY_OF_WEEK),
                Optional.of(A_MEALKIT_TYPE)));
        List<String> expectedSubscriptionIds = List.of(subscriptionId.getUUID().toString());

        SubscriptionsPayload subscriptionsPayload = subscriberService.getAllSubscriptions(A_SUBSCRIBER_ID);

        assertEquals(expectedSubscriptionIds, subscriptionsPayload.subscriptions().stream().map(SubscriptionPayload::subscriptionId).toList());
    }

    @Test
    public void givenSubscriberWithSubscription_whenGettingSubscription_shouldReturnSubscription() {
        subscriberService.createSubscriber(A_SUBSCRIBER_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);
        SubscriptionUniqueIdentifier subscriptionId = subscriberService.createSubscription(
            new SubscriptionQuery(A_WEEKLY_SUBSCRIPTION_TYPE, A_SUBSCRIBER_ID, Optional.of(A_LOCATION_ID), Optional.of(A_DAY_OF_WEEK),
                Optional.of(A_MEALKIT_TYPE)));

        SubscriptionPayload subscriptionPayload = subscriberService.getSubscription(A_SUBSCRIBER_ID, subscriptionId);

        assertEquals(subscriptionId.getUUID().toString(), subscriptionPayload.subscriptionId());
    }

    @Test
    public void givenSubscriberWithSubscription_whenGettingCurrentOrders_shouldReturnCurrentOrders() {
        subscriberService.createSubscriber(A_SUBSCRIBER_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);
        subscriberService.createSubscription(
            new SubscriptionQuery(A_WEEKLY_SUBSCRIPTION_TYPE, A_SUBSCRIBER_ID, Optional.of(A_LOCATION_ID), Optional.of(A_DAY_OF_WEEK),
                Optional.of(A_MEALKIT_TYPE)));

        OrdersPayload currentOrders = subscriberService.getCurrentOrders(A_SUBSCRIBER_ID);

        assertEquals(1, currentOrders.orders().size());
    }

    @Test
    public void givenSubscriberWithWeeklySubscription_whenConfirmingSubscription_shouldConfirmCurrentOrder() {
        subscriberService.createSubscriber(A_SUBSCRIBER_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);
        SubscriptionUniqueIdentifier subscriptionId = subscriberService.createSubscription(
            new SubscriptionQuery(A_WEEKLY_SUBSCRIPTION_TYPE, A_SUBSCRIBER_ID, Optional.of(A_LOCATION_ID), Optional.of(A_DAY_OF_WEEK),
                Optional.of(A_MEALKIT_TYPE)));

        subscriberService.confirm(A_SUBSCRIBER_ID, subscriptionId);

        OrdersPayload currentOrders = subscriberService.getCurrentOrders(A_SUBSCRIBER_ID);
        assertEquals(OrderStatus.CONFIRMED.name(), currentOrders.orders().get(0).orderStatus());
    }

    @Test
    public void givenSubscriberWithSporadicSubscription_whenConfirmingSubscription_shouldPutCurrentOrderInPreparation() {
        subscriberService.createSubscriber(A_SUBSCRIBER_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);
        SubscriptionUniqueIdentifier subscriptionId = subscriberService.createSubscription(
            new SubscriptionQuery(A_SPORADIC_SUBSCRIPTION_TYPE, A_SUBSCRIBER_ID, Optional.of(A_LOCATION_ID), Optional.of(A_DAY_OF_WEEK),
                Optional.of(A_MEALKIT_TYPE)));

        subscriberService.confirm(A_SUBSCRIBER_ID, subscriptionId);

        OrdersPayload currentOrders = subscriberService.getCurrentOrders(A_SUBSCRIBER_ID);
        assertEquals(OrderStatus.IN_PREPARATION.name(), currentOrders.orders().get(0).orderStatus());
    }

    @Test
    public void givenSubscriberWithWeeklySubscription_whenDecliningSubscription_shouldDeclineCurrentOrder() {
        subscriberService.createSubscriber(A_SUBSCRIBER_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);
        SubscriptionUniqueIdentifier subscriptionId = subscriberService.createSubscription(
            new SubscriptionQuery(A_WEEKLY_SUBSCRIPTION_TYPE, A_SUBSCRIBER_ID, Optional.of(A_LOCATION_ID), Optional.of(A_DAY_OF_WEEK),
                Optional.of(A_MEALKIT_TYPE)));

        subscriberService.decline(A_SUBSCRIBER_ID, subscriptionId);

        OrdersPayload currentOrders = subscriberService.getCurrentOrders(A_SUBSCRIBER_ID);
        assertEquals(OrderStatus.DECLINED.name(), currentOrders.orders().get(0).orderStatus());
    }

    @Test
    public void whenAddingCard_shouldAddCardToSubscriber() {
        subscriberService.createSubscriber(A_SUBSCRIBER_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);

        subscriberService.addCard(A_SUBSCRIBER_ID, A_CARD_NUMBER);

        ProfilePayload retrievedProfile = subscriberService.getSubscriberProfile(A_SUBSCRIBER_ID);
        assertEquals(A_CARD_NUMBER.value(), retrievedProfile.cardNumber());
    }
}
