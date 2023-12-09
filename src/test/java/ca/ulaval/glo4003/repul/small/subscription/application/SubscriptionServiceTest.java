package ca.ulaval.glo4003.repul.small.subscription.application;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.CanceledCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.fixture.subscription.OrderFixture;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriptionFixture;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.subscription.application.SubscriptionService;
import ca.ulaval.glo4003.repul.subscription.application.exception.SubscriptionNotFoundException;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrderPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.application.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    private static final DayOfWeek A_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final String A_MEALKIT_TYPE = "STANDARD";
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final SubscriptionQuery A_SUBSCRIPTION_QUERY = new SubscriptionQuery(A_DELIVERY_LOCATION_ID, A_DAY_OF_WEEK, A_MEALKIT_TYPE);
    private static final SubscriptionUniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriptionUniqueIdentifier AN_ID_NOT_MATCHING_ANY_SUBSCRIPTION =
        new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final SubscriberUniqueIdentifier ANOTHER_ACCOUNT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier A_MEALKIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final MealKitUniqueIdentifier ANOTHER_MEALKIT_ID = new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate();
    private static final Optional<LockerId> OPTIONAL_OF_A_LOCKER_ID = Optional.of(new LockerId("some id", 1));
    private static final LockerId A_LOCKER_ID = new LockerId("123", 4);
    private SubscriptionService subscriptionService;

    @Mock
    private Order mockOrder;
    @Mock
    private Subscription mockSubscription;
    @Mock
    private Subscription otherMockSubscription;
    @Mock
    private SubscriptionRepository mockSubscriptionRepository;
    @Mock
    private SubscriptionFactory mockSubscriptionFactory;
    @Mock
    private RepULEventBus mockEventBus;
    @Mock
    private PaymentService mockPaymentService;

    @BeforeEach
    public void createSubscriptionService() {
        subscriptionService = new SubscriptionService(mockSubscriptionRepository, mockSubscriptionFactory, mockPaymentService, mockEventBus);
    }

    @Test
    public void whenCreatingSubscription_shouldCreateSubscription() {
        when(mockSubscriptionFactory.createSubscription(any(SubscriberUniqueIdentifier.class), any(DeliveryLocationId.class), any(DayOfWeek.class),
            any(MealKitType.class))).thenReturn(mockSubscription);

        subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);

        verify(mockSubscriptionFactory).createSubscription(any(SubscriberUniqueIdentifier.class), eq(DeliveryLocationId.VACHON),
            eq(A_DAY_OF_WEEK), eq(MealKitType.valueOf(A_MEALKIT_TYPE)));
    }

    @Test
    public void whenCreatingSubscription_shouldSaveSubscription() {
        when(mockSubscriptionFactory.createSubscription(any(SubscriberUniqueIdentifier.class), any(DeliveryLocationId.class), any(DayOfWeek.class),
            any(MealKitType.class))).thenReturn(mockSubscription);

        subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);

        verify(mockSubscriptionRepository).save(mockSubscription);
    }

    @Test
    public void whenCreatingSubscription_shouldReturnSubscriptionId() {
        when(mockSubscriptionFactory.createSubscription(any(SubscriberUniqueIdentifier.class), any(DeliveryLocationId.class), any(DayOfWeek.class),
            any(MealKitType.class))).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriptionId()).thenReturn(A_SUBSCRIPTION_ID);

        UniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);

        assertEquals(A_SUBSCRIPTION_ID, subscriptionId);
    }

    @Test
    public void whenHandlingMealKitPickedUpByUserEvent_shouldGetSubscriptionsByOrderIdInRepository() {
        MealKitPickedUpByUserEvent event = new MealKitPickedUpByUserEvent(A_MEALKIT_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);

        subscriptionService.handleMealKitPickedUpByUserEvent(event);

        verify(mockSubscriptionRepository).getSubscriptionByOrderId(A_MEALKIT_ID);
    }

    @Test
    public void whenHandlingMealKitPickedUpByUserEvent_shouldMarkOrderAsPickedUp() {
        MealKitPickedUpByUserEvent event = new MealKitPickedUpByUserEvent(A_MEALKIT_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);

        subscriptionService.handleMealKitPickedUpByUserEvent(event);

        verify(mockSubscription).markOrderAsPickedUp(A_MEALKIT_ID);
    }

    @Test
    public void whenHandlingMealKitPickedUpByUserEvent_shouldSaveSubscription() {
        MealKitPickedUpByUserEvent event = new MealKitPickedUpByUserEvent(A_MEALKIT_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);

        subscriptionService.handleMealKitPickedUpByUserEvent(event);

        verify(mockSubscriptionRepository).save(mockSubscription);
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldGetSubscriptionsByOrderIdInRepository() {
        MealKitsCookedEvent event = new MealKitsCookedEvent("a location", List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(otherMockSubscription);

        subscriptionService.handleMealKitsCookedEvent(event);

        verify(mockSubscriptionRepository).getSubscriptionByOrderId(A_MEALKIT_ID);
        verify(mockSubscriptionRepository).getSubscriptionByOrderId(ANOTHER_MEALKIT_ID);
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldMarkSubscriptionsAsToDeliver() {
        MealKitsCookedEvent event = new MealKitsCookedEvent("a location", List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(otherMockSubscription);

        subscriptionService.handleMealKitsCookedEvent(event);

        verify(mockSubscription).markCurrentOrderAsToDeliver();
        verify(otherMockSubscription).markCurrentOrderAsToDeliver();
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldGetSubscriptionByOrderIdInRepository() {
        RecallCookedMealKitEvent recallCookedMealKitEvent = new RecallCookedMealKitEvent(A_MEALKIT_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);

        subscriptionService.handleRecallCookedMealKitEvent(recallCookedMealKitEvent);

        verify(mockSubscriptionRepository).getSubscriptionByOrderId(A_MEALKIT_ID);
    }

    @Test
    public void whenHandlingRecallCookedMealKitEvent_shouldMarkSubscriptionAsToCookO() {
        RecallCookedMealKitEvent recallCookedMealKitEvent = new RecallCookedMealKitEvent(A_MEALKIT_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);

        subscriptionService.handleRecallCookedMealKitEvent(recallCookedMealKitEvent);

        verify(mockSubscription).markCurrentOrderAsToCook();
    }

    @Test
    public void whenHandlingRecallCookedMealKitEvent_shouldSaveSubscription() {
        RecallCookedMealKitEvent recallCookedMealKitEvent = new RecallCookedMealKitEvent(A_MEALKIT_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);

        subscriptionService.handleRecallCookedMealKitEvent(recallCookedMealKitEvent);

        verify(mockSubscriptionRepository).save(mockSubscription);
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldSaveSubscriptionsInRepository() {
        MealKitsCookedEvent event = new MealKitsCookedEvent("a location", List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(otherMockSubscription);

        subscriptionService.handleMealKitsCookedEvent(event);

        verify(mockSubscriptionRepository).save(mockSubscription);
        verify(mockSubscriptionRepository).save(otherMockSubscription);
    }

    @Test
    public void whenHandlingPickedUpCargoEvent_shouldGetSubscriptionsByOrderIdInRepository() {
        PickedUpCargoEvent event = new PickedUpCargoEvent(List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(otherMockSubscription);

        subscriptionService.handlePickedUpCargoEvent(event);

        verify(mockSubscriptionRepository).getSubscriptionByOrderId(A_MEALKIT_ID);
        verify(mockSubscriptionRepository).getSubscriptionByOrderId(ANOTHER_MEALKIT_ID);
    }

    @Test
    public void whenHandlingPickedUpCargoEvent_shouldMarkSubscriptionsAsInDelivery() {
        PickedUpCargoEvent event = new PickedUpCargoEvent(List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(otherMockSubscription);

        subscriptionService.handlePickedUpCargoEvent(event);

        verify(mockSubscription).markCurrentOrderAsInDelivery();
        verify(otherMockSubscription).markCurrentOrderAsInDelivery();
    }

    @Test
    public void whenHandlingPickedUpCargoEvent_shouldSaveSubscriptionsInRepository() {
        PickedUpCargoEvent event = new PickedUpCargoEvent(List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(otherMockSubscription);

        subscriptionService.handlePickedUpCargoEvent(event);

        verify(mockSubscriptionRepository).save(mockSubscription);
        verify(mockSubscriptionRepository).save(otherMockSubscription);
    }

    @Test
    public void whenHandlingCanceledCargoEvent_shouldGetSubscriptionsByOrderIdInRepository() {
        CanceledCargoEvent event = new CanceledCargoEvent(List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(otherMockSubscription);

        subscriptionService.handleCanceledCargoEvent(event);

        verify(mockSubscriptionRepository).getSubscriptionByOrderId(A_MEALKIT_ID);
        verify(mockSubscriptionRepository).getSubscriptionByOrderId(ANOTHER_MEALKIT_ID);
    }

    @Test
    public void whenHandlingCanceledCargoEvent_shouldMarkMatchingOrdersAsToDeliver() {
        CanceledCargoEvent event = new CanceledCargoEvent(List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(otherMockSubscription);

        subscriptionService.handleCanceledCargoEvent(event);

        verify(mockSubscription).markCurrentOrderAsToDeliver();
        verify(otherMockSubscription).markCurrentOrderAsToDeliver();
    }

    @Test
    public void whenHandlingCanceledCargoEvent_shouldSaveSubscriptionsInRepository() {
        CanceledCargoEvent event = new CanceledCargoEvent(List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(otherMockSubscription);

        subscriptionService.handleCanceledCargoEvent(event);

        verify(mockSubscriptionRepository).save(mockSubscription);
        verify(mockSubscriptionRepository).save(otherMockSubscription);
    }

    @Test
    public void whenHandlingConfirmedDeliveryEvent_shouldGetSubscriptionsByOrderIdInRepository() {
        ConfirmedDeliveryEvent event = new ConfirmedDeliveryEvent(A_MEALKIT_ID, A_DELIVERY_LOCATION_ID, OPTIONAL_OF_A_LOCKER_ID, LocalTime.now());
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);

        subscriptionService.handleConfirmedDeliveryEvent(event);

        verify(mockSubscriptionRepository).getSubscriptionByOrderId(A_MEALKIT_ID);
    }

    @Test
    public void whenHandlingConfirmedDeliveryEvent_shouldMarkMatchingOrdersAsToPickup() {
        ConfirmedDeliveryEvent event = new ConfirmedDeliveryEvent(A_MEALKIT_ID, A_DELIVERY_LOCATION_ID, OPTIONAL_OF_A_LOCKER_ID, LocalTime.now());
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);

        subscriptionService.handleConfirmedDeliveryEvent(event);

        verify(mockSubscription).markCurrentOrderAsToPickUp();
    }

    @Test
    public void whenHandlingConfirmedDeliveryEvent_shouldSaveSubscriptionsInRepository() {
        ConfirmedDeliveryEvent event = new ConfirmedDeliveryEvent(A_MEALKIT_ID, A_DELIVERY_LOCATION_ID, OPTIONAL_OF_A_LOCKER_ID, LocalTime.now());
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);

        subscriptionService.handleConfirmedDeliveryEvent(event);

        verify(mockSubscriptionRepository).save(mockSubscription);
    }

    @Test
    public void whenHandlingRecalledDeliveryEvent_shouldGetSubscriptionsByOrderIdInRepository() {
        RecalledDeliveryEvent event = new RecalledDeliveryEvent(A_MEALKIT_ID, A_LOCKER_ID, A_DELIVERY_LOCATION_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);

        subscriptionService.handleRecalledDeliveryEvent(event);

        verify(mockSubscriptionRepository).getSubscriptionByOrderId(A_MEALKIT_ID);
    }

    @Test
    public void whenHandlingRecalledDeliveryEvent_shouldMarkMatchingOrdersAsInDelivery() {
        RecalledDeliveryEvent event = new RecalledDeliveryEvent(A_MEALKIT_ID, A_LOCKER_ID, A_DELIVERY_LOCATION_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);

        subscriptionService.handleRecalledDeliveryEvent(event);

        verify(mockSubscription).markCurrentOrderAsInDelivery();
    }

    @Test
    public void whenHandlingRecalledDeliveryEvent_shouldSaveSubscriptionsInRepository() {
        RecalledDeliveryEvent event = new RecalledDeliveryEvent(A_MEALKIT_ID, A_LOCKER_ID, A_DELIVERY_LOCATION_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(mockSubscription);

        subscriptionService.handleRecalledDeliveryEvent(event);

        verify(mockSubscriptionRepository).save(mockSubscription);
    }

    @Test
    public void whenGettingSubscriptions_shouldGetSubscriptionsFromRepository() {
        subscriptionService.getSubscriptions(AN_ACCOUNT_ID);

        verify(mockSubscriptionRepository).getBySubscriberId(AN_ACCOUNT_ID);
    }

    @Test
    public void whenGettingSubscriptions_shouldReturnMatchingSubscriptionsPayload() {
        Subscription subscription = new SubscriptionFixture().build();
        SubscriptionsPayload expectedPayload = new SubscriptionsPayload(List.of(SubscriptionPayload.from(subscription)));
        when(mockSubscriptionRepository.getBySubscriberId(AN_ACCOUNT_ID)).thenReturn(List.of(subscription));

        SubscriptionsPayload subscriptionsPayload = subscriptionService.getSubscriptions(AN_ACCOUNT_ID);

        assertEquals(expectedPayload, subscriptionsPayload);
    }

    @Test
    public void whenGettingSubscriptionById_shouldGetSubscriptionFromRepository() {
        when(mockSubscriptionRepository.getById(A_SUBSCRIPTION_ID)).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);

        subscriptionService.getSubscriptionById(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(mockSubscriptionRepository).getById(A_SUBSCRIPTION_ID);
    }

    @Test
    public void whenGettingSubscriptionById_shouldReturnMatchingSubscriptionsPayload() {
        Subscription subscription = new SubscriptionFixture().withSubscriberId(AN_ACCOUNT_ID).build();
        SubscriptionPayload expectedPayload = SubscriptionPayload.from(subscription);
        when(mockSubscriptionRepository.getById(subscription.getSubscriptionId())).thenReturn(subscription);

        SubscriptionPayload subscriptionPayload = subscriptionService.getSubscriptionById(AN_ACCOUNT_ID, subscription.getSubscriptionId());

        assertEquals(expectedPayload, subscriptionPayload);
    }

    @Test
    public void whenConfirmingNextMealKitForSubscription_shouldGetSubscriptionFromRepository() {
        when(mockSubscriptionRepository.getById(A_SUBSCRIPTION_ID)).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        when(mockSubscription.confirmNextMealKit()).thenReturn(mockOrder);

        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(mockSubscriptionRepository).getById(A_SUBSCRIPTION_ID);
    }

    @Test
    public void whenConfirmingNextMealKitForSubscription_shouldConfirmNextMealKitForSubscription() {
        when(mockSubscriptionRepository.getById(A_SUBSCRIPTION_ID)).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        when(mockSubscription.confirmNextMealKit()).thenReturn(mockOrder);

        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(mockSubscription).confirmNextMealKit();
    }

    @Test
    public void whenConfirmingNextMealKitForSubscription_shouldSaveSubscription() {
        when(mockSubscriptionRepository.getById(A_SUBSCRIPTION_ID)).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        when(mockSubscription.confirmNextMealKit()).thenReturn(mockOrder);

        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(mockSubscriptionRepository).save(mockSubscription);
    }

    @Test
    public void givenSubscriptionNotForThisSubscriber_whenConfirmingNextMealKitForSubscription_shouldThrow() {
        when(mockSubscriptionFactory.createSubscription(any(SubscriberUniqueIdentifier.class), any(DeliveryLocationId.class), any(DayOfWeek.class),
            any(MealKitType.class))).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        when(mockSubscriptionRepository.getById(subscriptionId)).thenReturn(mockSubscription);

        assertThrows(SubscriptionNotFoundException.class, () -> subscriptionService.confirmNextMealKitForSubscription(ANOTHER_ACCOUNT_ID, subscriptionId));
    }

    @Test
    public void whenDecliningNextMealKitForSubscription_shouldGetSubscriptionFromRepository() {
        when(mockSubscriptionRepository.getById(A_SUBSCRIPTION_ID)).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        when(mockSubscription.declineNextMealKit()).thenReturn(mockOrder);

        subscriptionService.declineNextMealKitForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(mockSubscriptionRepository).getById(A_SUBSCRIPTION_ID);
    }

    @Test
    public void whenDecliningNextMealKitForSubscription_shouldDeclineNextMealKitForSubscription() {
        when(mockSubscriptionRepository.getById(A_SUBSCRIPTION_ID)).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        when(mockSubscription.declineNextMealKit()).thenReturn(mockOrder);

        subscriptionService.declineNextMealKitForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(mockSubscription).declineNextMealKit();
    }

    @Test
    public void whenDecliningNextMealKitForSubscription_shouldSaveSubscription() {
        when(mockSubscriptionRepository.getById(A_SUBSCRIPTION_ID)).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        when(mockSubscription.declineNextMealKit()).thenReturn(mockOrder);

        subscriptionService.declineNextMealKitForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(mockSubscriptionRepository).save(mockSubscription);
    }

    @Test
    public void givenSubscriptionNotForThisSubscriber_whenDecliningNextMealKitForSubscription_shouldThrow() {
        when(mockSubscriptionFactory.createSubscription(any(SubscriberUniqueIdentifier.class), any(DeliveryLocationId.class), any(DayOfWeek.class),
            any(MealKitType.class))).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        when(mockSubscriptionRepository.getById(subscriptionId)).thenReturn(mockSubscription);

        assertThrows(SubscriptionNotFoundException.class, () -> subscriptionService.confirmNextMealKitForSubscription(ANOTHER_ACCOUNT_ID, subscriptionId));
    }

    @Test
    public void whenGettingCurrentOrders_shouldGetSubscriptionsFromRepository() {
        subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);

        verify(mockSubscriptionRepository).getBySubscriberId(AN_ACCOUNT_ID);
    }

    @Test
    public void whenGettingCurrentOrders_shouldReturnMatchingOrdersPayload() {
        Order order = new OrderFixture().build();
        Subscription subscription = new SubscriptionFixture().addOrder(order).build();
        OrdersPayload expectedPayload = new OrdersPayload(List.of(OrderPayload.from(order)));
        when(mockSubscriptionRepository.getBySubscriberId(AN_ACCOUNT_ID)).thenReturn(List.of(subscription));

        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);

        assertEquals(expectedPayload, ordersPayload);
    }
}
