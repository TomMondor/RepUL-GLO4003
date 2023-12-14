package ca.ulaval.glo4003.repul.medium.subscription;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.fixture.subscription.OrderFixture;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriptionFixture;
import ca.ulaval.glo4003.repul.subscription.application.SubscriptionService;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.domain.exception.NoNextOrderInSubscriptionException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderCannotBeDeclinedException;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrderStatus;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrdersFactory;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.infrastructure.LogPaymentService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SubscriptionServiceTest {
    private static final DeliveryLocationId A_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final DeliveryLocationId ANOTHER_LOCATION_ID = DeliveryLocationId.PEPS;
    private static final DayOfWeek A_DAY_STRING = DayOfWeek.from(LocalDate.now().plusDays(3));
    private static final String A_MEALKIT_TYPE = "STANDARD";
    private static final SubscriberUniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier ANOTHER_ACCOUNT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final Semester CURRENT_SEMESTER = new Semester(new SemesterCode("A23"), LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2));
    private RepULEventBus eventBus;
    private PaymentService paymentService;
    private SubscriptionService subscriptionService;

    @BeforeEach
    public void createService() {
        eventBus = new GuavaEventBus();
        paymentService = new LogPaymentService();
        SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();

        UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class);
        OrdersFactory ordersFactory = new OrdersFactory(mealKitUniqueIdentifierFactory);
        SubscriptionFactory subscriptionFactory = new SubscriptionFactory(
            new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class), ordersFactory,
            List.of(CURRENT_SEMESTER), List.of(A_LOCATION_ID, ANOTHER_LOCATION_ID));

        subscriptionService = new SubscriptionService(subscriptionRepository, subscriptionFactory, paymentService, eventBus, ordersFactory);
        eventBus.register(subscriptionService);
    }

    @Test
    public void whenCreatingSubscription_shouldPersistSubscription() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_LOCATION_ID,
            A_DAY_STRING, MealKitType.valueOf(A_MEALKIT_TYPE));

        SubscriptionsPayload persistedSubscriptions = subscriptionService.getSubscriptions(AN_ACCOUNT_ID);
        assertEquals(subscriptionId.getUUID().toString(), persistedSubscriptions.subscriptions().get(0).subscriptionId());
    }

    @Test
    public void whenCreatingSporadicSubscription_shouldPersistSubscription() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSporadicSubscription(AN_ACCOUNT_ID);

        SubscriptionsPayload persistedSubscriptions = subscriptionService.getSubscriptions(AN_ACCOUNT_ID);
        assertEquals(subscriptionId.getUUID().toString(), persistedSubscriptions.subscriptions().get(0).subscriptionId());
    }

    @Test
    public void givenCreatedSubscriptions_whenGettingSubscriptions_shouldReturnSubscriptionsInAccount() {
        SubscriptionUniqueIdentifier firstSubscriptionId = subscriptionService.createSubscription(
            AN_ACCOUNT_ID, A_LOCATION_ID, A_DAY_STRING, MealKitType.valueOf(A_MEALKIT_TYPE));
        SubscriptionUniqueIdentifier secondSubscriptionId = subscriptionService.createSporadicSubscription(AN_ACCOUNT_ID);
        subscriptionService.createSubscription(ANOTHER_ACCOUNT_ID, A_LOCATION_ID, A_DAY_STRING, MealKitType.valueOf(A_MEALKIT_TYPE));

        SubscriptionsPayload subscriptions = subscriptionService.getSubscriptions(AN_ACCOUNT_ID);

        assertEquals(2, subscriptions.subscriptions().size());

        List<String> expectedSubscriptionIds = List.of(firstSubscriptionId.getUUID().toString(), secondSubscriptionId.getUUID().toString());
        assertTrue(expectedSubscriptionIds.contains(subscriptions.subscriptions().get(0).subscriptionId()));
        assertTrue(expectedSubscriptionIds.contains(subscriptions.subscriptions().get(1).subscriptionId()));
    }

    @Test
    public void whenConfirmingNextMealKitForSubscription_shouldUpdateMatchingOrderInSubscription() {
        SubscriptionUniqueIdentifier subscriptionId =
            subscriptionService.createSubscription(AN_ACCOUNT_ID, A_LOCATION_ID, A_DAY_STRING, MealKitType.valueOf(A_MEALKIT_TYPE));

        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.CONFIRMED.toString(), updatedOrdersPayload.orders().get(0).orderStatus());
    }

    @Test
    public void whenConfirmingNextMealKitForSporadicSubscription_shouldCreateOneOrderInSubscription() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSporadicSubscription(AN_ACCOUNT_ID);
        int previousOrdersCount = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID).orders().size();

        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(previousOrdersCount + 1, updatedOrdersPayload.orders().size());
    }

    @Test
    public void whenConfirmingNextMealKitForSporadicSubscription_shouldCreateOrderDirectlyAsToCook() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSporadicSubscription(AN_ACCOUNT_ID);

        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.TO_COOK.toString(), updatedOrdersPayload.orders().get(0).orderStatus());
    }

    @Test
    public void whenDecliningNextMealKitForSubscription_shouldUpdateMatchingOrderInSubscription() {
        SubscriptionUniqueIdentifier subscriptionId =
            subscriptionService.createSubscription(AN_ACCOUNT_ID, A_LOCATION_ID, A_DAY_STRING, MealKitType.valueOf(A_MEALKIT_TYPE));

        subscriptionService.declineNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.DECLINED.toString(), updatedOrdersPayload.orders().get(0).orderStatus());
    }

    @Test
    public void givenNoConfirmedMealKit_whenDecliningNextMealKitForSporadicSubscription_shouldThrowNoNextOrderInSubscriptionException() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSporadicSubscription(AN_ACCOUNT_ID);

        assertThrows(NoNextOrderInSubscriptionException.class, () -> subscriptionService.declineNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId));
    }

    @Test
    public void givenAConfirmedMealKit_whenDecliningNextMealKitForSporadicSubscription_shouldThrowOrderCannotBeDeclinedException() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSporadicSubscription(AN_ACCOUNT_ID);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);

        assertThrows(OrderCannotBeDeclinedException.class, () -> subscriptionService.declineNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId));
    }

    @Test
    public void whenGettingCurrentOrders_shouldReturnCurrentOrdersForSubscriptionsInAccount() {
        SubscriptionUniqueIdentifier subscriptionId =
            subscriptionService.createSubscription(AN_ACCOUNT_ID, A_LOCATION_ID, A_DAY_STRING, MealKitType.valueOf(A_MEALKIT_TYPE));
        subscriptionService.createSubscription(AN_ACCOUNT_ID, ANOTHER_LOCATION_ID, A_DAY_STRING, MealKitType.valueOf(A_MEALKIT_TYPE));
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);

        OrdersPayload currentOrders = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);

        assertEquals(2, currentOrders.orders().size());
        assertTrue(currentOrders.orders().stream().anyMatch(order -> order.orderStatus().equals(OrderStatus.CONFIRMED.toString())));
        assertTrue(currentOrders.orders().stream().anyMatch(order -> order.orderStatus().equals(OrderStatus.PENDING.toString())));
    }

    @Test
    public void whenProcessingConfirmedAndReadyToProcessOrders_shouldMarkThemAsToCook() {
        Order order = givenAConfirmedOrderReadyToBeProcessed();

        subscriptionService.processConfirmationForTheDay();

        assertEquals(OrderStatus.TO_COOK, order.getOrderStatus());
    }

    @Test
    public void whenProcessingPendingAndReadyToProcessOrders_shouldMarkThemAsDeclined() {
        Order order = givenAPendingOrderReadyToBeProcessed();

        subscriptionService.processConfirmationForTheDay();

        assertEquals(OrderStatus.DECLINED, order.getOrderStatus());
    }

    @Test
    public void whenProcessingPendingAndReadyToProcessOrders_shouldPublishMealKitConfirmedEvent() {
        givenAConfirmedOrderReadyToBeProcessedWithMockEventBus();

        subscriptionService.processConfirmationForTheDay();

        verify(eventBus).publish(any(MealKitConfirmedEvent.class));
    }

    private Order givenAConfirmedOrderReadyToBeProcessed() {
        SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();
        SubscriptionFixture subscriptionFixture = new SubscriptionFixture();
        OrderFixture orderFixture = new OrderFixture();
        Order order = orderFixture.withOrderStatus(OrderStatus.CONFIRMED).withDeliveryDate(LocalDate.now().plusDays(1)).build();
        Subscription subscription = subscriptionFixture.withOrders(List.of(order)).build();
        subscriptionRepository.save(subscription);
        UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class);
        OrdersFactory ordersFactory = new OrdersFactory(mealKitUniqueIdentifierFactory);
        SubscriptionFactory subscriptionFactory =
            new SubscriptionFactory(new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class),
                ordersFactory, List.of(CURRENT_SEMESTER), List.of(A_LOCATION_ID, ANOTHER_LOCATION_ID));
        subscriptionService = new SubscriptionService(subscriptionRepository, subscriptionFactory, paymentService, eventBus, ordersFactory);
        eventBus.register(subscriptionService);

        return order;
    }

    private Order givenAPendingOrderReadyToBeProcessed() {
        SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();
        SubscriptionFixture subscriptionFixture = new SubscriptionFixture();
        OrderFixture orderFixture = new OrderFixture();
        Order order = orderFixture.withOrderStatus(OrderStatus.PENDING).withDeliveryDate(LocalDate.now().plusDays(1)).build();
        Subscription subscription = subscriptionFixture.withOrders(List.of(order)).build();
        subscriptionRepository.save(subscription);
        UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class);
        OrdersFactory ordersFactory = new OrdersFactory(mealKitUniqueIdentifierFactory);
        SubscriptionFactory subscriptionFactory =
            new SubscriptionFactory(new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class),
                ordersFactory, List.of(CURRENT_SEMESTER), List.of(A_LOCATION_ID, ANOTHER_LOCATION_ID));
        subscriptionService = new SubscriptionService(subscriptionRepository, subscriptionFactory, paymentService, eventBus, ordersFactory);
        eventBus.register(subscriptionService);

        return order;
    }

    private Order givenAConfirmedOrderReadyToBeProcessedWithMockEventBus() {
        SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();
        SubscriptionFixture subscriptionFixture = new SubscriptionFixture();
        OrderFixture orderFixture = new OrderFixture();
        Order order = orderFixture.withOrderStatus(OrderStatus.CONFIRMED).withDeliveryDate(LocalDate.now().plusDays(1)).build();
        Subscription subscription = subscriptionFixture.withOrders(List.of(order)).build();
        subscriptionRepository.save(subscription);
        UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class);
        OrdersFactory ordersFactory = new OrdersFactory(mealKitUniqueIdentifierFactory);
        SubscriptionFactory subscriptionFactory =
            new SubscriptionFactory(new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class),
                ordersFactory, List.of(CURRENT_SEMESTER), List.of(A_LOCATION_ID, ANOTHER_LOCATION_ID));
        eventBus = mock(RepULEventBus.class);
        subscriptionService = new SubscriptionService(subscriptionRepository, subscriptionFactory, paymentService, eventBus, ordersFactory);
        eventBus.register(subscriptionService);

        return order;
    }
}
