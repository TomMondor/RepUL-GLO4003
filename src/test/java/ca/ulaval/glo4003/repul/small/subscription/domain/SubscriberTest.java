package ca.ulaval.glo4003.repul.small.subscription.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.Email;
import ca.ulaval.glo4003.repul.commons.domain.IDUL;
import ca.ulaval.glo4003.repul.commons.domain.SubscriberCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.config.Config;
import ca.ulaval.glo4003.repul.fixture.subscription.OrderFixture;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriptionFixture;
import ca.ulaval.glo4003.repul.subscription.application.exception.SubscriptionNotFoundException;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.exception.NoUpcomingOrderInSubscriptionException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderCannotBeConfirmedException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderCannotBeDeclinedException;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Birthdate;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Gender;
import ca.ulaval.glo4003.repul.subscription.domain.profile.Name;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.ProcessConfirmation;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.OrderFactory;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.OrderStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubscriberTest {
    public static final int DAYS_TO_CONFIRM = 2;
    public static final Order A_PASSED_ORDER = new OrderFixture()
        .withDeliveryDate(LocalDate.now().minusDays(1)).build();
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final IDUL AN_IDUL = new IDUL("ALMAT69");
    private static final Name A_NAME = new Name("John Doe");
    private static final Birthdate A_BIRTHDATE = new Birthdate(LocalDate.parse("1990-01-01"));
    private static final Gender A_GENDER = Gender.OTHER;
    private static final Email AN_EMAIL = new Email("anEmail@ulaval.ca");
    private static final Subscription A_SUBSCRIPTION = new SubscriptionFixture().build();
    private static final Subscription A_SPORADIC_SUBSCRIPTION = new SubscriptionFixture().withFrequency(Optional.empty()).build();
    private static final Subscription ANOTHER_SUBSCRIPTION = new SubscriptionFixture().build();
    private static final SubscriptionUniqueIdentifier AN_INVALID_SUBSCRIPTION_ID = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
    private static final SubscriberCardNumber A_CARD_NUMBER = new SubscriberCardNumber("123456789");
    private static final Order AN_ACTIVE_ORDER = new OrderFixture().withDeliveryDate(LocalDate.now().plusDays(1)).build();
    private static final Order A_FUTURE_ORDER = new OrderFixture().withDeliveryDate(LocalDate.now().plusDays(8)).build();
    private static final Order A_CHANGEABLE_PENDING_ORDER =
        new OrderFixture().withDeliveryDate(LocalDate.now().plusDays(DAYS_TO_CONFIRM + 1))
            .withOrderStatus(OrderStatus.PENDING).build();
    private static final Order A_NON_CHANGEABLE_PENDING_ORDER =
        new OrderFixture().withDeliveryDate(LocalDate.now().plusDays(1))
            .withOrderStatus(OrderStatus.PENDING).build();
    private static final Order A_CHANGEABLE_CONFIRMED_ORDER =
        new OrderFixture().withDeliveryDate(LocalDate.now().plusDays(DAYS_TO_CONFIRM + 1))
            .withOrderStatus(OrderStatus.CONFIRMED).build();
    private static final Order A_NON_CHANGEABLE_CONFIRMED_ORDER =
        new OrderFixture().withDeliveryDate(LocalDate.now().plusDays(1))
            .withOrderStatus(OrderStatus.CONFIRMED).build();
    private static final Order A_CHANGEABLE_DECLINED_ORDER =
        new OrderFixture().withDeliveryDate(LocalDate.now().plusDays(DAYS_TO_CONFIRM + 1))
            .withOrderStatus(OrderStatus.DECLINED).build();
    private static final Order A_NON_CHANGEABLE_DECLINED_ORDER =
        new OrderFixture().withDeliveryDate(LocalDate.now().plusDays(1)).withOrderStatus(OrderStatus.DECLINED).build();
    private static final Order AN_IN_PREPARATION_ORDER =
        new OrderFixture().withDeliveryDate(LocalDate.now().plusDays(1)).withOrderStatus(OrderStatus.IN_PREPARATION).build();
    private static final Order AN_IN_DELIVERY_ORDER = new OrderFixture().withDeliveryDate(LocalDate.now()).withOrderStatus(OrderStatus.IN_DELIVERY).build();
    private static final Order A_READY_FOR_PICKUP_ORDER =
        new OrderFixture().withDeliveryDate(LocalDate.now()).withOrderStatus(OrderStatus.READY_FOR_PICK_UP).build();
    private static final Order A_PICKED_UP_ORDER = new OrderFixture().withDeliveryDate(LocalDate.now()).withOrderStatus(OrderStatus.PICKED_UP).build();

    @Mock
    private OrderFactory orderFactory;
    @Mock
    private PaymentService paymentService;

    private Subscriber subscriber;

    @BeforeAll
    public static void setupConfig() {
        LocalTime openingTime = LocalTime.of(9, 0);
        Config.initialize(openingTime, Duration.ofDays(DAYS_TO_CONFIRM));
    }

    @BeforeEach
    public void createSubscriber() {
        subscriber = new Subscriber(A_SUBSCRIBER_ID, AN_IDUL, A_NAME, A_BIRTHDATE, A_GENDER, AN_EMAIL);
    }

    @Test
    public void whenAddingSubscription_shouldAddSubscription() {
        subscriber.addSubscription(A_SUBSCRIPTION);

        assertEquals(A_SUBSCRIPTION, subscriber.getSubscription(A_SUBSCRIPTION.getSubscriptionId()));
    }

    @Test
    public void whenGettingAllSubscriptions_shouldReturnAllSubscriptions() {
        subscriber.addSubscription(A_SUBSCRIPTION);
        subscriber.addSubscription(ANOTHER_SUBSCRIPTION);

        assertEquals(2, subscriber.getAllSubscriptions().size());
    }

    @Test
    public void givenSubscriptionIdNotAssociatedToSubscriber_whenGettingSubscription_shouldThrowSubscriptionNotFound() {
        assertThrows(SubscriptionNotFoundException.class, () -> subscriber.getSubscription(AN_INVALID_SUBSCRIPTION_ID));
    }

    @Test
    public void whenSettingCardNumber_shouldAddItToProfile() {
        subscriber.setCardNumber(A_CARD_NUMBER);

        assertEquals(Optional.of(A_CARD_NUMBER), subscriber.getProfile().getCardNumber());
    }

    @Test
    public void givenCompletedNonSporadicSubscription_whenGettingCurrentOrders_shouldReturnEmptyList() {
        Subscription completedSubscription =
            new SubscriptionFixture().withOrders(List.of(A_PASSED_ORDER)).build();
        subscriber.addSubscription(completedSubscription);

        List<Order> currentOrders = subscriber.getCurrentOrders();

        assertTrue(currentOrders.isEmpty());
    }

    @Test
    public void givenActiveNonSporadicSubscription_whenGettingCurrentOrders_shouldReturnListWithOneOrder() {
        Subscription activeSubscription =
            new SubscriptionFixture().withOrders(List.of(AN_ACTIVE_ORDER)).build();
        subscriber.addSubscription(activeSubscription);

        List<Order> currentOrders = subscriber.getCurrentOrders();

        assertEquals(1, currentOrders.size());
    }

    @Test
    public void givenOneActiveNonSporadicSubscriptionWithManyRemainingOrdersAndSomePassedOrders_whenGettingCurrentOrders_shouldReturnListWithOneOrder() {
        Subscription activeSubscription =
            new SubscriptionFixture().withOrders(List.of(A_PASSED_ORDER, AN_ACTIVE_ORDER, A_FUTURE_ORDER)).build();
        subscriber.addSubscription(activeSubscription);

        List<Order> currentOrders = subscriber.getCurrentOrders();

        assertEquals(1, currentOrders.size());
        assertEquals(AN_ACTIVE_ORDER, currentOrders.get(0));
    }

    @Test
    public void givenMultipleActiveNonSporadicSubscriptions_whenGettingCurrentOrders_shouldReturnListWithMultipleOrders() {
        Subscription activeSubscription =
            new SubscriptionFixture().withOrders(List.of(AN_ACTIVE_ORDER)).build();
        Subscription anotherActiveSubscription =
            new SubscriptionFixture().withOrders(List.of(AN_ACTIVE_ORDER)).build();
        subscriber.addSubscription(activeSubscription);
        subscriber.addSubscription(anotherActiveSubscription);

        List<Order> currentOrders = subscriber.getCurrentOrders();

        assertEquals(2, currentOrders.size());
    }

    @Test
    public void givenCompletedSubscription_whenConfirmingSubscription_shouldThrowNoUpcomingOrderException() {
        Subscription aCompleteSubscription =
            new SubscriptionFixture().withOrders(List.of(A_PASSED_ORDER)).build();
        subscriber.addSubscription(aCompleteSubscription);

        assertThrows(
            NoUpcomingOrderInSubscriptionException.class,
            () -> subscriber.confirm(aCompleteSubscription.getSubscriptionId(), orderFactory, paymentService));
    }

    @Test
    public void givenNonSporadicSubscription_whenConfirmingSubscription_shouldReturnEmptyProcessConfirmation() {
        Subscription aSubscription =
            new SubscriptionFixture().withOrders(List.of(A_CHANGEABLE_CONFIRMED_ORDER)).build();
        subscriber.addSubscription(aSubscription);

        Optional<ProcessConfirmation> processConfirmation =
            subscriber.confirm(aSubscription.getSubscriptionId(), orderFactory, paymentService);

        assertFalse(processConfirmation.isPresent());
    }

    @Test
    public void givenNonSporadicSubscriptionNotAssociatedToSubscriber_whenConfirmingSubscription_shouldThrowSubscriptionNotFound() {
        assertThrows(SubscriptionNotFoundException.class, () -> subscriber.confirm(AN_INVALID_SUBSCRIPTION_ID, orderFactory, paymentService));
    }

    @Test
    public void givenNonSporadicSubscriptionWithActiveChangeablePendingOrder_whenConfirmingSubscription_shouldUpdateStatusToConfirmed() {
        Subscription aSubscriptionWithActiveChangeablePendingOrder =
            new SubscriptionFixture().withOrders(List.of(A_CHANGEABLE_PENDING_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithActiveChangeablePendingOrder);

        subscriber.confirm(aSubscriptionWithActiveChangeablePendingOrder.getSubscriptionId(), orderFactory, paymentService);

        assertEquals(OrderStatus.CONFIRMED, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }

    @Test
    public void givenNonSporadicSubscriptionWithActiveNonChangeablePendingOrder_whenConfirmingSubscription_shouldThrowOrderCannotBeConfirmedException() {
        Subscription aSubscriptionWithActiveNonChangeablePendingOrder =
            new SubscriptionFixture().withOrders(List.of(A_NON_CHANGEABLE_PENDING_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithActiveNonChangeablePendingOrder);

        assertThrows(
            OrderCannotBeConfirmedException.class,
            () -> subscriber.confirm(aSubscriptionWithActiveNonChangeablePendingOrder.getSubscriptionId(), orderFactory, paymentService));
    }

    @Test
    public void givenNonSporadicSubscriptionWithConfirmedOrder_whenConfirmingSubscription_shouldRemainAsConfirmed() {
        Subscription aSubscriptionWithActiveChangeableConfirmedOrder =
            new SubscriptionFixture().withOrders(List.of(A_CHANGEABLE_CONFIRMED_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithActiveChangeableConfirmedOrder);

        subscriber.confirm(aSubscriptionWithActiveChangeableConfirmedOrder.getSubscriptionId(), orderFactory, paymentService);

        assertEquals(OrderStatus.CONFIRMED, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }

    @Test
    public void givenNonSporadicSubscriptionWithActiveNonChangeableDeclinedOrder_whenConfirmingSubscription_shouldThrowOrderCannotBeConfirmedException() {
        Subscription aSubscriptionWithActiveNonChangeableDeclinedOrder =
            new SubscriptionFixture().withOrders(List.of(A_NON_CHANGEABLE_DECLINED_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithActiveNonChangeableDeclinedOrder);

        assertThrows(
            OrderCannotBeConfirmedException.class,
            () -> subscriber.confirm(aSubscriptionWithActiveNonChangeableDeclinedOrder.getSubscriptionId(), orderFactory, paymentService));
    }

    @Test
    public void givenNonSporadicSubscriptionWithActiveChangeableDeclinedOrder_whenConfirmingSubscription_shouldUpdateStatusToConfirmed() {
        Subscription aSubscriptionWithActiveChangeableDeclinedOrder =
            new SubscriptionFixture().withOrders(List.of(A_CHANGEABLE_DECLINED_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithActiveChangeableDeclinedOrder);

        subscriber.confirm(aSubscriptionWithActiveChangeableDeclinedOrder.getSubscriptionId(), orderFactory, paymentService);

        assertEquals(OrderStatus.CONFIRMED, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }

    @Test
    public void givenNonSporadicSubscriptionWithActiveInPreparationOrder_whenConfirmingSubscription_shouldThrowOrderCannotBeConfirmedException() {
        Subscription aSubscriptionWithActiveInPreparationOrder =
            new SubscriptionFixture().withOrders(List.of(AN_IN_PREPARATION_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithActiveInPreparationOrder);

        assertThrows(
            OrderCannotBeConfirmedException.class,
            () -> subscriber.confirm(aSubscriptionWithActiveInPreparationOrder.getSubscriptionId(), orderFactory, paymentService));
    }

    @Test
    public void givenNonSporadicSubscriptionWithActiveInDeliveryOrder_whenConfirmingSubscription_shouldThrowOrderCannotBeConfirmedException() {
        Subscription aSubscriptionWithActiveInDeliveryOrder =
            new SubscriptionFixture().withOrders(List.of(AN_IN_DELIVERY_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithActiveInDeliveryOrder);

        assertThrows(
            OrderCannotBeConfirmedException.class,
            () -> subscriber.confirm(aSubscriptionWithActiveInDeliveryOrder.getSubscriptionId(), orderFactory, paymentService));
    }

    @Test
    public void givenNonSporadicSubscriptionWithActiveReadyForPickUpOrder_whenConfirmingSubscription_shouldThrowOrderCannotBeConfirmedException() {
        Subscription aSubscriptionWithActiveReadyForPickUpOrder =
            new SubscriptionFixture().withOrders(List.of(A_READY_FOR_PICKUP_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithActiveReadyForPickUpOrder);

        assertThrows(
            OrderCannotBeConfirmedException.class,
            () -> subscriber.confirm(aSubscriptionWithActiveReadyForPickUpOrder.getSubscriptionId(), orderFactory, paymentService));
    }

    @Test
    public void givenNonSporadicSubscriptionWithActivePickedUpOrder_whenConfirmingSubscription_shouldThrowOrderCannotBeConfirmedException() {
        Subscription aSubscriptionWithActivePickedUpOrder =
            new SubscriptionFixture().withOrders(List.of(A_PICKED_UP_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithActivePickedUpOrder);

        assertThrows(
            OrderCannotBeConfirmedException.class,
            () -> subscriber.confirm(aSubscriptionWithActivePickedUpOrder.getSubscriptionId(), orderFactory, paymentService));
    }

    @Test
    public void givenSporadicSubscription_whenConfirming_shouldCreateNewOrder() {
        given(orderFactory.createSporadicOrder(any(), any())).willReturn(new OrderFixture().build());
        subscriber.addSubscription(A_SPORADIC_SUBSCRIPTION);

        subscriber.confirm(A_SPORADIC_SUBSCRIPTION.getSubscriptionId(), orderFactory, paymentService);

        assertEquals(1, subscriber.getCurrentOrders().size());
    }

    @Test
    public void givenSporadicSubscription_whenConfirming_shouldUpdateOrderStatusToInPreparation() {
        given(orderFactory.createSporadicOrder(any(), any())).willReturn(new OrderFixture().build());
        subscriber.addSubscription(A_SPORADIC_SUBSCRIPTION);

        subscriber.confirm(A_SPORADIC_SUBSCRIPTION.getSubscriptionId(), orderFactory, paymentService);

        assertEquals(OrderStatus.IN_PREPARATION, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }

    @Test
    public void givenSporadicSubscription_whenConfirming_shouldReturnProcessConfirmation() {
        Order order = new OrderFixture().build();
        given(orderFactory.createSporadicOrder(any(), any())).willReturn(order);
        subscriber.addSubscription(A_SPORADIC_SUBSCRIPTION);

        Optional<ProcessConfirmation> processConfirmation =
            subscriber.confirm(A_SPORADIC_SUBSCRIPTION.getSubscriptionId(), orderFactory, paymentService);

        assertEquals(order.getOrderId(), processConfirmation.get().confirmedOrders().get(0).getOrderId());
    }

    @Test
    public void givenCompletedSubscription_whenDeclining_shouldThrowNoUpcomingOrderException() {
        Subscription aCompleteSubscription =
            new SubscriptionFixture().withOrders(List.of(A_PASSED_ORDER)).build();
        subscriber.addSubscription(aCompleteSubscription);

        assertThrows(
            NoUpcomingOrderInSubscriptionException.class,
            () -> subscriber.decline(aCompleteSubscription.getSubscriptionId()));
    }

    @Test
    public void givenSubscriptionWithPendingOrder_whenDeclining_shouldUpdateStatusToDeclined() {
        Order aChangeablePendingOrder =
            new OrderFixture().withDeliveryDate(LocalDate.now().plusDays(DAYS_TO_CONFIRM + 1))
                .withOrderStatus(OrderStatus.PENDING).build();
        Subscription aSubscriptionWithPendingOrder =
            new SubscriptionFixture().withOrders(List.of(aChangeablePendingOrder)).build();
        subscriber.addSubscription(aSubscriptionWithPendingOrder);

        subscriber.decline(aSubscriptionWithPendingOrder.getSubscriptionId());

        assertEquals(OrderStatus.DECLINED, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }

    @Test
    public void givenSubscriptionWithChangeableConfirmedOrder_whenDeclining_shouldUpdateStatusToDeclined() {
        Subscription aSubscriptionWithChangeableConfirmedOrder =
            new SubscriptionFixture().withOrders(List.of(A_CHANGEABLE_CONFIRMED_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithChangeableConfirmedOrder);

        subscriber.decline(aSubscriptionWithChangeableConfirmedOrder.getSubscriptionId());

        assertEquals(OrderStatus.DECLINED, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }

    @Test
    public void givenSubscriptionWithNonChangeableConfirmedOrder_whenDeclining_shouldThrowOrderCannotBeDeclinedException() {
        Subscription aSubscriptionWithNonChangeableConfirmedOrder =
            new SubscriptionFixture().withOrders(List.of(A_NON_CHANGEABLE_CONFIRMED_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithNonChangeableConfirmedOrder);

        assertThrows(
            OrderCannotBeDeclinedException.class,
            () -> subscriber.decline(aSubscriptionWithNonChangeableConfirmedOrder.getSubscriptionId()));
    }

    @Test
    public void givenSubscriptionWithDeclinedOrder_whenDeclining_shouldRemainAsDeclined() {
        Subscription aSubscriptionWithDeclinedOrder =
            new SubscriptionFixture().withOrders(List.of(A_CHANGEABLE_DECLINED_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithDeclinedOrder);

        subscriber.decline(aSubscriptionWithDeclinedOrder.getSubscriptionId());

        assertEquals(OrderStatus.DECLINED, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }

    @Test
    public void givenSubscriptionWithInPreparationOrder_whenDeclining_shouldThrowOrderCannotBeDeclinedException() {
        Subscription aSubscriptionWithInPreparationOrder =
            new SubscriptionFixture().withOrders(List.of(AN_IN_PREPARATION_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithInPreparationOrder);

        assertThrows(
            OrderCannotBeDeclinedException.class,
            () -> subscriber.decline(aSubscriptionWithInPreparationOrder.getSubscriptionId()));
    }

    @Test
    public void givenSubscriptionWithInDeliveryOrder_whenDeclining_shouldThrowOrderCannotBeDeclinedException() {
        Subscription aSubscriptionWithInDeliveryOrder =
            new SubscriptionFixture().withOrders(List.of(AN_IN_DELIVERY_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithInDeliveryOrder);

        assertThrows(
            OrderCannotBeDeclinedException.class,
            () -> subscriber.decline(aSubscriptionWithInDeliveryOrder.getSubscriptionId()));
    }

    @Test
    public void givenSubscriptionWithReadyForPickUpOrder_whenDeclining_shouldThrowOrderCannotBeDeclinedException() {
        Subscription aSubscriptionWithReadyForPickUpOrder =
            new SubscriptionFixture().withOrders(List.of(A_READY_FOR_PICKUP_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithReadyForPickUpOrder);

        assertThrows(
            OrderCannotBeDeclinedException.class,
            () -> subscriber.decline(aSubscriptionWithReadyForPickUpOrder.getSubscriptionId()));
    }

    @Test
    public void givenSubscriptionWithPickedUpOrder_whenDeclining_shouldThrowOrderCannotBeDeclinedException() {
        Subscription aSubscriptionWithPickedUpOrder =
            new SubscriptionFixture().withOrders(List.of(A_PICKED_UP_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithPickedUpOrder);

        assertThrows(
            OrderCannotBeDeclinedException.class,
            () -> subscriber.decline(aSubscriptionWithPickedUpOrder.getSubscriptionId()));
    }

    @Test
    public void givenSubscriptionWithChangeablePendingOrder_whenProcessingOrders_shouldNotProcessOrder() {
        Order aChangeablePendingOrder =
            new OrderFixture().withDeliveryDate(LocalDate.now().plusDays(DAYS_TO_CONFIRM + 1))
                .withOrderStatus(OrderStatus.PENDING).build();
        Subscription aSubscriptionWithPendingOrder =
            new SubscriptionFixture().withOrders(List.of(aChangeablePendingOrder)).build();
        subscriber.addSubscription(aSubscriptionWithPendingOrder);

        subscriber.processOrders(paymentService);

        assertEquals(OrderStatus.PENDING, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }

    @Test
    public void givenSubscriptionWithNonChangeablePendingOrder_whenProcessingOrders_shouldChangeStatusToDeclined() {
        Subscription aSubscriptionWithNonChangeablePendingOrder =
            new SubscriptionFixture().withOrders(List.of(A_NON_CHANGEABLE_PENDING_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithNonChangeablePendingOrder);

        subscriber.processOrders(paymentService);

        assertEquals(OrderStatus.DECLINED, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }

    @Test
    public void givenSubscriptionWithChangeableConfirmedOrder_whenProcessingOrders_shouldNotProcessOrder() {
        Subscription aSubscriptionWithChangeableConfirmedOrder =
            new SubscriptionFixture().withOrders(List.of(A_CHANGEABLE_CONFIRMED_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithChangeableConfirmedOrder);

        subscriber.processOrders(paymentService);

        assertEquals(OrderStatus.CONFIRMED, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }

    @Test
    public void givenSubscriptionWithNonChangeableConfirmedOrder_whenProcessingOrders_shouldProcessOrder() {
        Order aNonChangeableConfirmedOrder =
            new OrderFixture().withDeliveryDate(LocalDate.now().plusDays(1)).withOrderStatus(OrderStatus.CONFIRMED).build();
        Subscription aSubscriptionWithNonChangeableConfirmedOrder =
            new SubscriptionFixture().withOrders(List.of(aNonChangeableConfirmedOrder)).build();
        subscriber.addSubscription(aSubscriptionWithNonChangeableConfirmedOrder);

        subscriber.processOrders(paymentService);

        verify(paymentService).pay(eq(subscriber.getSubscriberId()), eq(A_NON_CHANGEABLE_CONFIRMED_ORDER.getMealKitType()), any());
        assertEquals(OrderStatus.IN_PREPARATION, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }

    @Test
    public void givenSubscriptionWithNonChangeableConfirmedOrder_whenProcessingOrders_shouldReturnOrderInProcessConfirmation() {
        Subscription aSubscriptionWithNonChangeableConfirmedOrder =
            new SubscriptionFixture().withOrders(List.of(A_NON_CHANGEABLE_CONFIRMED_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithNonChangeableConfirmedOrder);

        List<ProcessConfirmation> processConfirmations = subscriber.processOrders(paymentService);

        assertEquals(A_NON_CHANGEABLE_CONFIRMED_ORDER.getOrderId(), processConfirmations.get(0).confirmedOrders().get(0).getOrderId());
    }

    @Test
    public void givenSubscriptionWithDeclinedOrder_whenProcessingOrders_shouldNotProcessOrder() {
        Subscription aSubscriptionWithChangeableDeclinedOrder =
            new SubscriptionFixture().withOrders(List.of(A_CHANGEABLE_DECLINED_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithChangeableDeclinedOrder);

        subscriber.processOrders(paymentService);

        assertEquals(OrderStatus.DECLINED, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }

    @Test
    public void givenSubscriptionWithInPreparationOrder_whenProcessingOrders_shouldNotProcessOrder() {
        Subscription aSubscriptionWithInPreparationOrder =
            new SubscriptionFixture().withOrders(List.of(AN_IN_PREPARATION_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithInPreparationOrder);

        subscriber.processOrders(paymentService);

        assertEquals(OrderStatus.IN_PREPARATION, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }

    @Test
    public void givenSubscriptionWithInDeliveryOrder_whenProcessingOrders_shouldNotProcessOrder() {
        Subscription aSubscriptionWithInDeliveryOrder =
            new SubscriptionFixture().withOrders(List.of(AN_IN_DELIVERY_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithInDeliveryOrder);

        subscriber.processOrders(paymentService);

        assertEquals(OrderStatus.IN_DELIVERY, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }

    @Test
    public void givenSubscriptionWithReadyForPickUpOrder_whenProcessingOrders_shouldNotProcessOrder() {
        Subscription aSubscriptionWithReadyForPickUpOrder =
            new SubscriptionFixture().withOrders(List.of(A_READY_FOR_PICKUP_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithReadyForPickUpOrder);

        subscriber.processOrders(paymentService);

        assertEquals(OrderStatus.READY_FOR_PICK_UP, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }

    @Test
    public void givenSubscriptionWithPickedUpOrder_whenProcessingOrders_shouldNotProcessOrder() {
        Subscription aSubscriptionWithPickedUpOrder =
            new SubscriptionFixture().withOrders(List.of(A_PICKED_UP_ORDER)).build();
        subscriber.addSubscription(aSubscriptionWithPickedUpOrder);

        subscriber.processOrders(paymentService);

        assertEquals(OrderStatus.PICKED_UP, subscriber.getCurrentOrders().get(0).getOrderStatus());
    }
}
