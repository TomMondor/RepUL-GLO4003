package ca.ulaval.glo4003.repul.medium.subscription;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.commons.application.RepULEventBus;
import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.commons.infrastructure.GuavaEventBus;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitDto;
import ca.ulaval.glo4003.repul.cooking.application.event.MealKitsCookedEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.CanceledCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.ConfirmedDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.PickedUpCargoEvent;
import ca.ulaval.glo4003.repul.delivery.application.event.RecalledDeliveryEvent;
import ca.ulaval.glo4003.repul.delivery.domain.LockerId;
import ca.ulaval.glo4003.repul.lockerauthorization.application.event.MealKitPickedUpByUserEvent;
import ca.ulaval.glo4003.repul.subscription.api.SubscriberEventHandler;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.application.SubscriptionService;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.domain.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrderStatus;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrdersFactory;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.infrastructure.InMemorySubscriptionRepository;
import ca.ulaval.glo4003.repul.subscription.infrastructure.LogPaymentService;
import ca.ulaval.glo4003.repul.user.application.event.UserCreatedEvent;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriberEventHandlerTest {
    private static final UniqueIdentifier A_USER_ID = new UniqueIdentifierFactory<>(UniqueIdentifier.class).generate();
    private static final IDUL AN_IDUL = new IDUL("ALMAT69");
    private static final Name A_NAME = new Name("John Doe");
    private static final Birthdate A_BIRTHDATE = new Birthdate(LocalDate.now().minusYears(18));
    private static final Gender A_GENDER = Gender.MAN;
    private static final Email AN_EMAIL = new Email("anEmail@ulaval.ca");
    private static final Semester CURRENT_SEMESTER = new Semester(new SemesterCode("A23"), LocalDate.now().minusMonths(1), LocalDate.now().plusMonths(2));
    private static final DeliveryLocationId A_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final DeliveryLocationId ANOTHER_LOCATION_ID = DeliveryLocationId.PEPS;
    private static final SubscriberUniqueIdentifier AN_ACCOUNT_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final DayOfWeek A_DAY_STRING = DayOfWeek.from(LocalDate.now().plusDays(3));
    private static final MealKitType A_MEAL_KIT_TYPE = MealKitType.STANDARD;
    private static final LockerId A_LOCKER_ID = new LockerId("123", 4);

    private SubscriberEventHandler subscriberEventHandler;
    private RepULEventBus eventBus;
    private SubscriberService subscriberService;
    private SubscriberRepository subscriberRepository;
    private SubscriptionService subscriptionService;
    private SubscriptionRepository subscriptionRepository;

    @BeforeEach
    public void createUserEventHandler() {
        subscriberRepository = new InMemorySubscriberRepository();
        subscriptionRepository = new InMemorySubscriptionRepository();
        eventBus = new GuavaEventBus();
        subscriberService = new SubscriberService(subscriberRepository, new SubscriberFactory(), eventBus);
        SubscriptionRepository subscriptionRepository = new InMemorySubscriptionRepository();
        UniqueIdentifierFactory<MealKitUniqueIdentifier> mealKitUniqueIdentifierFactory =
            new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class);
        SubscriptionFactory subscriptionFactory =
            new SubscriptionFactory(mealKitUniqueIdentifierFactory, new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class),
                new OrdersFactory(mealKitUniqueIdentifierFactory), List.of(CURRENT_SEMESTER), List.of(A_LOCATION_ID, ANOTHER_LOCATION_ID));
        subscriptionService = new SubscriptionService(subscriptionRepository, subscriptionFactory, new LogPaymentService(), eventBus);
        subscriberEventHandler = new SubscriberEventHandler(subscriberService, subscriptionService);
        eventBus.register(subscriberEventHandler);
    }

    @Test
    public void whenHandlingUserCreatedEvent_shouldAddSubscriberToRepository() {
        UserCreatedEvent userCreatedEvent = new UserCreatedEvent(A_USER_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);

        eventBus.publish(userCreatedEvent);

        SubscriberUniqueIdentifier subscriberId = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generateFrom(A_USER_ID);
        assertDoesNotThrow(() -> subscriberRepository.getById(subscriberId));
    }

    @Test
    public void whenHandlingPickedUpCargoEvent_shouldMarkMatchingOrdersAsInDelivery() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID,
            A_LOCATION_ID, A_DAY_STRING, A_MEAL_KIT_TYPE);
        SubscriptionUniqueIdentifier otherSubscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID,
            ANOTHER_LOCATION_ID, A_DAY_STRING, A_MEAL_KIT_TYPE);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, otherSubscriptionId);
        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        List<MealKitUniqueIdentifier> orderIds = ordersPayload.orders().stream()
            .map(orderPayload ->
                new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(orderPayload.orderId())
            ).toList();
        PickedUpCargoEvent event = new PickedUpCargoEvent(orderIds);

        eventBus.publish(event);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.IN_DELIVERY.toString(), updatedOrdersPayload.orders().get(0).orderStatus());
        assertEquals(OrderStatus.IN_DELIVERY.toString(), updatedOrdersPayload.orders().get(1).orderStatus());
    }

    @Test
    public void whenHandlingMealKitsCookedEvent_shouldMarkMatchingOrdersAsToDeliver() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID,
            A_LOCATION_ID, A_DAY_STRING, A_MEAL_KIT_TYPE);
        SubscriptionUniqueIdentifier otherSubscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID,
            ANOTHER_LOCATION_ID, A_DAY_STRING, A_MEAL_KIT_TYPE);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, otherSubscriptionId);
        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        List<MealKitUniqueIdentifier> orderIds =  ordersPayload.orders().stream()
            .map(orderPayload ->
                new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(orderPayload.orderId())
            ).toList();
        List<MealKitDto> mealKitDtos = orderIds.stream().map(orderId -> new MealKitDto(orderId, true)).toList();
        MealKitsCookedEvent event = new MealKitsCookedEvent(A_LOCATION_ID.toString(), mealKitDtos);

        eventBus.publish(event);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.TO_DELIVER.toString(), updatedOrdersPayload.orders().get(0).orderStatus());
        assertEquals(OrderStatus.TO_DELIVER.toString(), updatedOrdersPayload.orders().get(1).orderStatus());
    }

    @Test
    public void whenHandlingMealKitPickedUpByUserEvent_shouldMarkMatchingOrderAsPickedUp() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID,
            A_LOCATION_ID, A_DAY_STRING, A_MEAL_KIT_TYPE);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);
        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        MealKitUniqueIdentifier orderId = ordersPayload.orders().stream()
            .map(orderPayload ->
                new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(orderPayload.orderId())
            ).toList().get(0);
        MealKitPickedUpByUserEvent event = new MealKitPickedUpByUserEvent(orderId);

        eventBus.publish(event);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.PICKED_UP.toString(), updatedOrdersPayload.orders().get(0).orderStatus());
    }

    @Test
    public void whenHandlingCanceledCargoEvent_shouldMarkMatchingOrdersAsToDeliver() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID,
            A_LOCATION_ID, A_DAY_STRING, A_MEAL_KIT_TYPE);
        SubscriptionUniqueIdentifier otherSubscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID,
            ANOTHER_LOCATION_ID, A_DAY_STRING, A_MEAL_KIT_TYPE);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, otherSubscriptionId);
        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        List<MealKitUniqueIdentifier> orderIds = ordersPayload.orders().stream()
            .map(orderPayload ->
                new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(orderPayload.orderId())
            ).toList();
        CanceledCargoEvent event = new CanceledCargoEvent(orderIds);

        eventBus.publish(event);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.TO_DELIVER.toString(), updatedOrdersPayload.orders().get(0).orderStatus());
        assertEquals(OrderStatus.TO_DELIVER.toString(), updatedOrdersPayload.orders().get(1).orderStatus());
    }

    @Test
    public void whenHandlingRecalledDeliveryEvent_shouldMarkMatchingOrdersAsInDelivery() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID,
            A_LOCATION_ID, A_DAY_STRING, A_MEAL_KIT_TYPE);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);
        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        MealKitUniqueIdentifier orderId = ordersPayload.orders().stream()
            .map(orderPayload ->
                new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(orderPayload.orderId())
            ).toList().get(0);
        RecalledDeliveryEvent event = new RecalledDeliveryEvent(orderId, A_LOCKER_ID, A_LOCATION_ID);

        eventBus.publish(event);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.IN_DELIVERY.toString(), updatedOrdersPayload.orders().get(0).orderStatus());
    }

    @Test
    public void whenHandlingConfirmedDeliveryEvent_shouldMarkMatchingOrdersAsToPickup() {
        SubscriptionUniqueIdentifier subscriptionId = subscriptionService.createSubscription(AN_ACCOUNT_ID,
            A_LOCATION_ID, A_DAY_STRING, A_MEAL_KIT_TYPE);
        subscriptionService.confirmNextMealKitForSubscription(AN_ACCOUNT_ID, subscriptionId);
        OrdersPayload ordersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        MealKitUniqueIdentifier orderId = ordersPayload.orders().stream()
            .map(orderPayload ->
                new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generateFrom(orderPayload.orderId())
            ).toList().get(0);
        ConfirmedDeliveryEvent event = new ConfirmedDeliveryEvent(orderId, A_LOCATION_ID, Optional.of(A_LOCKER_ID), LocalTime.now());

        eventBus.publish(event);

        OrdersPayload updatedOrdersPayload = subscriptionService.getCurrentOrders(AN_ACCOUNT_ID);
        assertEquals(OrderStatus.TO_PICKUP.toString(), updatedOrdersPayload.orders().get(0).orderStatus());
    }
}
