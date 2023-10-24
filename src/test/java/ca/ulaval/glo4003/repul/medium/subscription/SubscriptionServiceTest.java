package ca.ulaval.glo4003.repul.medium.subscription;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.shipping.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.subscription.application.SubscriptionService;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrderPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.application.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.subscription.domain.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrderStatus;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubscriptionServiceTest {
    private static final String A_LOCATION_STRING = "VACHON";
    private static final String ANOTHER_LOCATION_STRING = "DESJARDINS";
    private static final String A_DAY_STRING = DayOfWeek.from(LocalDate.now().plusDays(3)).toString();
    private static final String A_MEALKIT_TYPE = "STANDARD";
    private static final SubscriptionQuery A_SUBSCRIPTION_QUERY = new SubscriptionQuery(A_LOCATION_STRING, A_DAY_STRING, A_MEALKIT_TYPE);
    private static final SubscriptionQuery ANOTHER_SUBSCRIPTION_QUERY = new SubscriptionQuery(ANOTHER_LOCATION_STRING, A_DAY_STRING, A_MEALKIT_TYPE);
    private static final UniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier ANOTHER_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final Semester CURRENT_SEMESTER = new Semester(new SemesterCode("A23"), LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2));
    private static final DeliveryLocationId A_LOCATION_ID = new DeliveryLocationId(A_LOCATION_STRING);
    private static final DeliveryLocationId ANOTHER_LOCATION_ID = new DeliveryLocationId(ANOTHER_LOCATION_STRING);
    private RepULEventBus eventBus;
    private SubscriptionService subscriptionService;

    @BeforeEach
    public void createService() {
        eventBus = new GuavaEventBus();
        SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();
        SubscriptionFactory subscriptionFactory =
            new SubscriptionFactory(new UniqueIdentifierFactory(), List.of(CURRENT_SEMESTER), List.of(A_LOCATION_ID, ANOTHER_LOCATION_ID));
        subscriptionService = new SubscriptionService(subscriptionRepository, subscriptionFactory, eventBus);
        eventBus.register(subscriptionService);
    }

    @Test
    public void whenCreatingSubscription_shouldPersistSubscription() {
        UniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);

        SubscriptionsPayload persistedSubscriptions = subscriptionService.getSubscriptions(AN_ACCOUNT_ID);
        assertEquals(subscriptionId, persistedSubscriptions.subscriptions().get(0).subscriptionId());
    }

    @Test
    public void givenCreatedSubscriptions_whenGettingSubscriptions_shouldReturnSubscriptionsInAccount() {
        UniqueIdentifier firstSubscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        UniqueIdentifier secondSubscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, ANOTHER_SUBSCRIPTION_QUERY);
        subscriptionService.createSubscription(ANOTHER_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);

        SubscriptionsPayload subscriptions = subscriptionService.getSubscriptions(AN_ACCOUNT_ID);

        assertEquals(2, subscriptions.subscriptions().size());

        List<UniqueIdentifier> expectedSubscriptionIds = List.of(firstSubscriptionId, secondSubscriptionId);
        assertTrue(expectedSubscriptionIds.contains(subscriptions.subscriptions().get(0).subscriptionId()));
        assertTrue(expectedSubscriptionIds.contains(subscriptions.subscriptions().get(1).subscriptionId()));
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldMarkMatchingOrdersAsToDeliver() {
        UniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        UniqueIdentifier otherSubscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, ANOTHER_SUBSCRIPTION_QUERY);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, otherSubscriptionId);
        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        List<UniqueIdentifier> orderIds = ordersPayload.orders().stream().map(OrderPayload::orderId).toList();
        MealKitsCookedEvent event = new MealKitsCookedEvent(A_LOCATION_STRING, orderIds);

        eventBus.publish(event);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.TO_DELIVER, updatedOrdersPayload.orders().get(0).orderStatus());
        assertEquals(OrderStatus.TO_DELIVER, updatedOrdersPayload.orders().get(1).orderStatus());
    }

    @Test
    public void whenHandlingPickedUpCargoEvent_shouldMarkMatchingOrdersAsInDelivery() {
        UniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        UniqueIdentifier otherSubscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, ANOTHER_SUBSCRIPTION_QUERY);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, otherSubscriptionId);
        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        List<UniqueIdentifier> orderIds = ordersPayload.orders().stream().map(OrderPayload::orderId).toList();
        PickedUpCargoEvent event = new PickedUpCargoEvent(orderIds);

        eventBus.publish(event);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.IN_DELIVERY, updatedOrdersPayload.orders().get(0).orderStatus());
        assertEquals(OrderStatus.IN_DELIVERY, updatedOrdersPayload.orders().get(1).orderStatus());
    }

    @Test
    public void whenConfirmingNextMealKitForSubscription_shouldUpdateMatchingOrderInSubscription() {
        UniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);

        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.TO_COOK, updatedOrdersPayload.orders().get(0).orderStatus());
    }

    @Test
    public void whenDecliningNextMealKitForSubscription_shouldUpdateMatchingOrderInSubscription() {
        UniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);

        subscriptionService.declineNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.DECLINED, updatedOrdersPayload.orders().get(0).orderStatus());
    }

    @Test
    public void whenGettingCurrentOrders_shouldReturnCurrentOrdersForSubscriptionsInAccount() {
        UniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        subscriptionService.createSubscription(AN_ACCOUNT_ID, ANOTHER_SUBSCRIPTION_QUERY);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);

        OrdersPayload currentOrders = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);

        assertEquals(2, currentOrders.orders().size());
        assertTrue(currentOrders.orders().stream().anyMatch(order -> order.orderStatus().equals(OrderStatus.TO_COOK)));
        assertTrue(currentOrders.orders().stream().anyMatch(order -> order.orderStatus().equals(OrderStatus.PENDING)));
    }
}
