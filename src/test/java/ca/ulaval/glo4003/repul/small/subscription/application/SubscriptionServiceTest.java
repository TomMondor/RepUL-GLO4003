package ca.ulaval.glo4003.repul.small.subscription.application;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.cooking.application.event.RecallCookedMealKitEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.CanceledCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.fixture.subscription.OrderFixture;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriptionFixture;
import ca.ulaval.glo4003.repul.subscription.application.SubscriptionService;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.subscription.application.exception.OrderNotFoundException;
import ca.ulaval.glo4003.repul.subscription.application.exception.SubscriptionNotFoundException;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrderPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.application.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionServiceTest {
    private static final String A_LOCATION_STRING = "VACHON";
    private static final String A_DAY_STRING = "MONDAY";
    private static final String A_MEALKIT_TYPE = "STANDARD";
    private static final SubscriptionQuery A_SUBSCRIPTION_QUERY = new SubscriptionQuery(A_LOCATION_STRING, A_DAY_STRING, A_MEALKIT_TYPE);
    private static final UniqueIdentifier A_SUBSCRIPTION_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier AN_ID_NOT_MATCHING_ANY_SUBSCRIPTION = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier ANOTHER_ACCOUNT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier A_MEALKIT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier ANOTHER_MEALKIT_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = new DeliveryLocationId(A_LOCATION_STRING);
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

    @BeforeEach
    public void createSubscriptionService() {
        subscriptionService = new SubscriptionService(mockSubscriptionRepository, mockSubscriptionFactory, mockEventBus);
    }

    @Test
    public void whenCreatingSubscription_shouldCreateSubscription() {
        when(mockSubscriptionFactory.createSubscription(any(UniqueIdentifier.class), any(DeliveryLocationId.class), any(DayOfWeek.class),
            any(MealKitType.class))).thenReturn(mockSubscription);

        subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);

        verify(mockSubscriptionFactory).createSubscription(any(UniqueIdentifier.class), eq(new DeliveryLocationId(A_LOCATION_STRING)),
            eq(DayOfWeek.valueOf(A_DAY_STRING)), eq(MealKitType.valueOf(A_MEALKIT_TYPE)));
    }

    @Test
    public void whenCreatingSubscription_shouldSaveOrUpdateSubscription() {
        when(mockSubscriptionFactory.createSubscription(any(UniqueIdentifier.class), any(DeliveryLocationId.class), any(DayOfWeek.class),
            any(MealKitType.class))).thenReturn(mockSubscription);

        subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);

        verify(mockSubscriptionRepository).saveOrUpdate(mockSubscription);
    }

    @Test
    public void whenCreatingSubscription_shouldReturnSubscriptionId() {
        when(mockSubscriptionFactory.createSubscription(any(UniqueIdentifier.class), any(DeliveryLocationId.class), any(DayOfWeek.class),
            any(MealKitType.class))).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriptionId()).thenReturn(A_SUBSCRIPTION_ID);

        UniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);

        assertEquals(A_SUBSCRIPTION_ID, subscriptionId);
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldGetSubscriptionsByOrderIdInRepository() {
        MealKitsCookedEvent event = new MealKitsCookedEvent("a location", List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(Optional.of(otherMockSubscription));

        subscriptionService.handleMealKitsCookedEvent(event);

        verify(mockSubscriptionRepository).getSubscriptionByOrderId(A_MEALKIT_ID);
        verify(mockSubscriptionRepository).getSubscriptionByOrderId(ANOTHER_MEALKIT_ID);
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldMarkSubscriptionsAsToDeliver() {
        MealKitsCookedEvent event = new MealKitsCookedEvent("a location", List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(Optional.of(otherMockSubscription));

        subscriptionService.handleMealKitsCookedEvent(event);

        verify(mockSubscription).markCurrentOrderAsToDeliver();
        verify(otherMockSubscription).markCurrentOrderAsToDeliver();
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldGetSubscriptionByOrderIdInRepository() {
        RecallCookedMealKitEvent recallCookedMealKitEvent = new RecallCookedMealKitEvent(A_MEALKIT_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));

        subscriptionService.handleRecallCookedMealKitEvent(recallCookedMealKitEvent);

        verify(mockSubscriptionRepository).getSubscriptionByOrderId(A_MEALKIT_ID);
    }

    @Test
    public void whenHandlingRecallCookedMealKitEvent_shouldMarkSubscriptionAsToCookO() {
        RecallCookedMealKitEvent recallCookedMealKitEvent = new RecallCookedMealKitEvent(A_MEALKIT_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));

        subscriptionService.handleRecallCookedMealKitEvent(recallCookedMealKitEvent);

        verify(mockSubscription).markCurrentOrderAsToCook();
    }

    @Test
    public void whenHandlingRecallCookedMealKitEvent_shouldSaveOrUpdateSubscription() {
        RecallCookedMealKitEvent recallCookedMealKitEvent = new RecallCookedMealKitEvent(A_MEALKIT_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));

        subscriptionService.handleRecallCookedMealKitEvent(recallCookedMealKitEvent);

        verify(mockSubscriptionRepository).saveOrUpdate(mockSubscription);
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldSaveSubscriptionsInRepository() {
        MealKitsCookedEvent event = new MealKitsCookedEvent("a location", List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(Optional.of(otherMockSubscription));

        subscriptionService.handleMealKitsCookedEvent(event);

        verify(mockSubscriptionRepository).saveOrUpdate(mockSubscription);
        verify(mockSubscriptionRepository).saveOrUpdate(otherMockSubscription);
    }

    @Test
    public void givenInexistantMealKit_whenHandlingMealKitsCookedEvent_shouldThrowOrderNotFoundException() {
        MealKitsCookedEvent event = new MealKitsCookedEvent("a location", List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> subscriptionService.handleMealKitsCookedEvent(event));
    }

    @Test
    public void whenHandlingPickedUpCargoEvent_shouldGetSubscriptionsByOrderIdInRepository() {
        PickedUpCargoEvent event = new PickedUpCargoEvent(List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(Optional.of(otherMockSubscription));

        subscriptionService.handlePickedUpCargoEvent(event);

        verify(mockSubscriptionRepository).getSubscriptionByOrderId(A_MEALKIT_ID);
        verify(mockSubscriptionRepository).getSubscriptionByOrderId(ANOTHER_MEALKIT_ID);
    }

    @Test
    public void givenInexistantMealKit_whenHandlingPickedUpCargoEvent_shouldThrowOrderNotFoundException() {
        PickedUpCargoEvent event = new PickedUpCargoEvent(List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> subscriptionService.handlePickedUpCargoEvent(event));
    }

    @Test
    public void whenHandlingPickedUpCargoEvent_shouldMarkSubscriptionsAsInDelivery() {
        PickedUpCargoEvent event = new PickedUpCargoEvent(List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(Optional.of(otherMockSubscription));

        subscriptionService.handlePickedUpCargoEvent(event);

        verify(mockSubscription).markCurrentOrderAsInDelivery();
        verify(otherMockSubscription).markCurrentOrderAsInDelivery();
    }

    @Test
    public void whenHandlingPickedUpCargoEvent_shouldSaveSubscriptionsInRepository() {
        PickedUpCargoEvent event = new PickedUpCargoEvent(List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(Optional.of(otherMockSubscription));

        subscriptionService.handlePickedUpCargoEvent(event);

        verify(mockSubscriptionRepository).saveOrUpdate(mockSubscription);
        verify(mockSubscriptionRepository).saveOrUpdate(otherMockSubscription);
    }

    @Test
    public void whenHandlingCanceledCargoEvent_shouldGetSubscriptionsByOrderIdInRepository() {
        CanceledCargoEvent event = new CanceledCargoEvent(List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(Optional.of(otherMockSubscription));

        subscriptionService.handleCanceledCargoEvent(event);

        verify(mockSubscriptionRepository).getSubscriptionByOrderId(A_MEALKIT_ID);
        verify(mockSubscriptionRepository).getSubscriptionByOrderId(ANOTHER_MEALKIT_ID);
    }

    @Test
    public void givenInexistantMealKit_whenHandlingCanceledCargoEvent_shouldThrowOrderNotFoundException() {
        CanceledCargoEvent event = new CanceledCargoEvent(List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> subscriptionService.handleCanceledCargoEvent(event));
    }

    @Test
    public void whenHandlingCanceledCargoEvent_shouldMarkMatchingOrdersAsToDeliver() {
        CanceledCargoEvent event = new CanceledCargoEvent(List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(Optional.of(otherMockSubscription));

        subscriptionService.handleCanceledCargoEvent(event);

        verify(mockSubscription).markCurrentOrderAsToDeliver();
        verify(otherMockSubscription).markCurrentOrderAsToDeliver();
    }

    @Test
    public void whenHandlingCanceledCargoEvent_shouldSaveSubscriptionsInRepository() {
        CanceledCargoEvent event = new CanceledCargoEvent(List.of(A_MEALKIT_ID, ANOTHER_MEALKIT_ID));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscriptionRepository.getSubscriptionByOrderId(ANOTHER_MEALKIT_ID)).thenReturn(Optional.of(otherMockSubscription));

        subscriptionService.handleCanceledCargoEvent(event);

        verify(mockSubscriptionRepository).saveOrUpdate(mockSubscription);
        verify(mockSubscriptionRepository).saveOrUpdate(otherMockSubscription);
    }

    @Test
    public void whenHandlingConfirmedDeliveryEvent_shouldGetSubscriptionsByOrderIdInRepository() {
        ConfirmedDeliveryEvent event = new ConfirmedDeliveryEvent(A_MEALKIT_ID, A_DELIVERY_LOCATION_ID, Optional.empty(), LocalTime.now());
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));

        subscriptionService.handleConfirmedDeliveryEvent(event);

        verify(mockSubscriptionRepository).getSubscriptionByOrderId(A_MEALKIT_ID);
    }

    @Test
    public void givenInexistantMealKit_whenHandlingConfirmedDeliveryEvent_shouldThrowOrderNotFoundException() {
        ConfirmedDeliveryEvent event = new ConfirmedDeliveryEvent(A_MEALKIT_ID, A_DELIVERY_LOCATION_ID, Optional.empty(), LocalTime.now());
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> subscriptionService.handleConfirmedDeliveryEvent(event));
    }

    @Test
    public void whenHandlingConfirmedDeliveryEvent_shouldMarkMatchingOrdersAsToPickup() {
        ConfirmedDeliveryEvent event = new ConfirmedDeliveryEvent(A_MEALKIT_ID, A_DELIVERY_LOCATION_ID, Optional.empty(), LocalTime.now());
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));

        subscriptionService.handleConfirmedDeliveryEvent(event);

        verify(mockSubscription).markCurrentOrderAsToPickUp();
    }

    @Test
    public void whenHandlingConfirmedDeliveryEvent_shouldSaveSubscriptionsInRepository() {
        ConfirmedDeliveryEvent event = new ConfirmedDeliveryEvent(A_MEALKIT_ID, A_DELIVERY_LOCATION_ID, Optional.empty(), LocalTime.now());
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));

        subscriptionService.handleConfirmedDeliveryEvent(event);

        verify(mockSubscriptionRepository).saveOrUpdate(mockSubscription);
    }

    @Test
    public void whenHandlingRecalledDeliveryEvent_shouldGetSubscriptionsByOrderIdInRepository() {
        RecalledDeliveryEvent event = new RecalledDeliveryEvent(A_MEALKIT_ID, A_LOCKER_ID, A_DELIVERY_LOCATION_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));

        subscriptionService.handleRecalledDeliveryEvent(event);

        verify(mockSubscriptionRepository).getSubscriptionByOrderId(A_MEALKIT_ID);
    }

    @Test
    public void givenInexistantMealKit_whenHandlingRecalledDeliveryEvent_shouldThrowOrderNotFoundException() {
        RecalledDeliveryEvent event = new RecalledDeliveryEvent(A_MEALKIT_ID, A_LOCKER_ID, A_DELIVERY_LOCATION_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> subscriptionService.handleRecalledDeliveryEvent(event));
    }

    @Test
    public void whenHandlingRecalledDeliveryEvent_shouldMarkMatchingOrdersAsInDelivery() {
        RecalledDeliveryEvent event = new RecalledDeliveryEvent(A_MEALKIT_ID, A_LOCKER_ID, A_DELIVERY_LOCATION_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));

        subscriptionService.handleRecalledDeliveryEvent(event);

        verify(mockSubscription).markCurrentOrderAsInDelivery();
    }

    @Test
    public void whenHandlingRecalledDeliveryEvent_shouldSaveSubscriptionsInRepository() {
        RecalledDeliveryEvent event = new RecalledDeliveryEvent(A_MEALKIT_ID, A_LOCKER_ID, A_DELIVERY_LOCATION_ID);
        when(mockSubscriptionRepository.getSubscriptionByOrderId(A_MEALKIT_ID)).thenReturn(Optional.of(mockSubscription));

        subscriptionService.handleRecalledDeliveryEvent(event);

        verify(mockSubscriptionRepository).saveOrUpdate(mockSubscription);
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
        when(mockSubscriptionRepository.getById(A_SUBSCRIPTION_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);

        subscriptionService.getSubscriptionById(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(mockSubscriptionRepository).getById(A_SUBSCRIPTION_ID);
    }

    @Test
    public void whenGettingSubscriptionById_shouldReturnMatchingSubscriptionsPayload() {
        Subscription subscription = new SubscriptionFixture().withSubscriberId(AN_ACCOUNT_ID).build();
        SubscriptionPayload expectedPayload = SubscriptionPayload.from(subscription);
        when(mockSubscriptionRepository.getById(subscription.getSubscriptionId())).thenReturn(Optional.of(subscription));

        SubscriptionPayload subscriptionPayload = subscriptionService.getSubscriptionById(AN_ACCOUNT_ID,
            subscription.getSubscriptionId());

        assertEquals(expectedPayload, subscriptionPayload);
    }

    @Test
    public void whenConfirmingNextMealKitForSubscription_shouldGetSubscriptionFromRepository() {
        when(mockSubscriptionRepository.getById(A_SUBSCRIPTION_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        when(mockSubscription.confirmNextMealKit()).thenReturn(mockOrder);

        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(mockSubscriptionRepository).getById(A_SUBSCRIPTION_ID);
    }

    @Test
    public void whenConfirmingNextMealKitForSubscription_shouldConfirmNextMealKitForSubscription() {
        when(mockSubscriptionRepository.getById(A_SUBSCRIPTION_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        when(mockSubscription.confirmNextMealKit()).thenReturn(mockOrder);

        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(mockSubscription).confirmNextMealKit();
    }

    @Test
    public void whenConfirmingNextMealKitForSubscription_shouldSaveOrUpdateSubscription() {
        when(mockSubscriptionRepository.getById(A_SUBSCRIPTION_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        when(mockSubscription.confirmNextMealKit()).thenReturn(mockOrder);

        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(mockSubscriptionRepository).saveOrUpdate(mockSubscription);
    }

    @Test
    public void whenConfirmingNextMealKitForSubscription_shouldPublishMatchingEvent() {
        when(mockSubscriptionRepository.getById(A_SUBSCRIPTION_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        when(mockSubscription.confirmNextMealKit()).thenReturn(mockOrder);

        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(mockEventBus).publish(any(MealKitConfirmedEvent.class));
    }

    @Test
    public void givenNonexistentSubscription_whenConfirmingNextMealKitForSubscription_shouldThrow() {
        assertThrows(SubscriptionNotFoundException.class,
            () -> subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, AN_ID_NOT_MATCHING_ANY_SUBSCRIPTION));
    }

    @Test
    public void givenSubscriptionNotForThisSubscriber_whenConfirmingNextMealKitForSubscription_shouldThrow() {
        when(mockSubscriptionFactory.createSubscription(any(UniqueIdentifier.class), any(DeliveryLocationId.class), any(DayOfWeek.class),
            any(MealKitType.class))).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        UniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        when(mockSubscriptionRepository.getById(subscriptionId)).thenReturn(Optional.of(mockSubscription));

        assertThrows(SubscriptionNotFoundException.class, () -> subscriptionService.confirmNextMealKitForSubscription(ANOTHER_ACCOUNT_ID, subscriptionId));
    }

    @Test
    public void whenDecliningNextMealKitForSubscription_shouldGetSubscriptionFromRepository() {
        when(mockSubscriptionRepository.getById(A_SUBSCRIPTION_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        when(mockSubscription.declineNextMealKit()).thenReturn(mockOrder);

        subscriptionService.declineNextMealKitForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(mockSubscriptionRepository).getById(A_SUBSCRIPTION_ID);
    }

    @Test
    public void whenDecliningNextMealKitForSubscription_shouldDeclineNextMealKitForSubscription() {
        when(mockSubscriptionRepository.getById(A_SUBSCRIPTION_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        when(mockSubscription.declineNextMealKit()).thenReturn(mockOrder);

        subscriptionService.declineNextMealKitForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(mockSubscription).declineNextMealKit();
    }

    @Test
    public void whenDecliningNextMealKitForSubscription_shouldSaveOrUpdateSubscription() {
        when(mockSubscriptionRepository.getById(A_SUBSCRIPTION_ID)).thenReturn(Optional.of(mockSubscription));
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        when(mockSubscription.declineNextMealKit()).thenReturn(mockOrder);

        subscriptionService.declineNextMealKitForSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_ID);

        verify(mockSubscriptionRepository).saveOrUpdate(mockSubscription);
    }

    @Test
    public void givenNonexistentSubscription_whenDecliningNextMealKitForSubscription_shouldThrow() {
        assertThrows(SubscriptionNotFoundException.class,
            () -> subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, AN_ID_NOT_MATCHING_ANY_SUBSCRIPTION));
    }

    @Test
    public void givenSubscriptionNotForThisSubscriber_whenDecliningNextMealKitForSubscription_shouldThrow() {
        when(mockSubscriptionFactory.createSubscription(any(UniqueIdentifier.class), any(DeliveryLocationId.class), any(DayOfWeek.class),
            any(MealKitType.class))).thenReturn(mockSubscription);
        when(mockSubscription.getSubscriberId()).thenReturn(AN_ACCOUNT_ID);
        UniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID, A_SUBSCRIPTION_QUERY);
        when(mockSubscriptionRepository.getById(subscriptionId)).thenReturn(Optional.of(mockSubscription));

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
