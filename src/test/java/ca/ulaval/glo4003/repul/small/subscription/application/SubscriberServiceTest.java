package ca.ulaval.glo4003.repul.small.subscription.application;

import java.time.DayOfWeek;
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
import ca.ulaval.glo4003.repul.commons.domain.SubscriberCardNumber;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.fixture.subscription.OrderFixture;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriberFixture;
import ca.ulaval.glo4003.repul.fixture.subscription.SubscriptionFixture;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;
import ca.ulaval.glo4003.repul.subscription.application.event.MealKitConfirmedEvent;
import ca.ulaval.glo4003.repul.subscription.application.event.SubscriberCardAddedEvent;
import ca.ulaval.glo4003.repul.subscription.application.exception.CardNumberAlreadyInUseException;
import ca.ulaval.glo4003.repul.subscription.application.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.ProfilePayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionPayload;
import ca.ulaval.glo4003.repul.subscription.application.payload.SubscriptionsPayload;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.Subscriber;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriberRepository;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionType;
import ca.ulaval.glo4003.repul.subscription.domain.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.ProcessConfirmationDto;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.OrderFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SubscriberServiceTest {
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifierFactory<>(SubscriberUniqueIdentifier.class).generate();
    private static final SubscriberCardNumber AN_UNASSIGNED_CARD_NUMBER = new SubscriberCardNumber("123456789");
    private static final SubscriberCardNumber AN_ASSIGNED_CARD_NUMBER = new SubscriberCardNumber("987654321");
    private static final DayOfWeek A_DAY_OF_WEEK = DayOfWeek.MONDAY;
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final DeliveryLocationId A_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final SubscriptionType A_WEEKLY_SUBSCRIPTION_TYPE = SubscriptionType.WEEKLY;
    private static final Subscription A_SUBSCRIPTION = new SubscriptionFixture().build();
    private static final Order AN_ORDER = new OrderFixture().build();

    private SubscriberService subscriberService;
    @Mock
    private SubscriberRepository subscriberRepository;
    @Mock
    private SubscriberFactory subscriberFactory;
    @Mock
    private SubscriptionFactory subscriptionFactory;
    @Mock
    private RepULEventBus eventBus;
    @Mock
    private PaymentService paymentService;
    @Mock
    private OrderFactory orderFactory;
    @Mock
    private Subscriber mockSubscriber;

    @BeforeEach

    public void createSubscriberService() {
        subscriberService = new SubscriberService(subscriberRepository, subscriberFactory, subscriptionFactory, eventBus, paymentService, orderFactory);
    }

    @Test
    public void whenGettingSubscriberProfile_shouldReturnSubscriberProfilePayload() {
        Subscriber subscriber = new SubscriberFixture().withSubscriberId(A_SUBSCRIBER_ID).build();
        given(subscriberRepository.getById(A_SUBSCRIBER_ID)).willReturn(subscriber);
        ProfilePayload expectedProfilePayload = ProfilePayload.from(subscriber.getProfile());

        ProfilePayload retrievedProfile = subscriberService.getSubscriberProfile(A_SUBSCRIBER_ID);

        assertEquals(expectedProfilePayload, retrievedProfile);
    }

    @Test
    public void whenCreatingSubscription_shouldReturnSubscriptionId() {
        Subscriber subscriber = new SubscriberFixture().withSubscriberId(A_SUBSCRIBER_ID).withCardNumber(AN_UNASSIGNED_CARD_NUMBER).build();
        given(subscriberRepository.getById(A_SUBSCRIBER_ID)).willReturn(subscriber);
        given(subscriptionFactory.createSubscription(any(SubscriptionQuery.class))).willReturn(A_SUBSCRIPTION);

        SubscriptionUniqueIdentifier subscriptionId =
            subscriberService.createSubscription(
                new SubscriptionQuery(A_WEEKLY_SUBSCRIPTION_TYPE, A_SUBSCRIBER_ID, Optional.of(A_DELIVERY_LOCATION_ID), Optional.of(A_DAY_OF_WEEK),
                    Optional.of(A_MEALKIT_TYPE)));

        assertEquals(A_SUBSCRIPTION.getSubscriptionId(), subscriptionId);
    }

    @Test
    public void whenGettingAllSubscriptions_shouldReturnMatchingSubscriptionsPayload() {
        given(subscriberRepository.getById(A_SUBSCRIBER_ID)).willReturn(mockSubscriber);
        given(mockSubscriber.getAllSubscriptions()).willReturn(List.of(A_SUBSCRIPTION));
        SubscriptionsPayload expectedSubscriptionsPayload = SubscriptionsPayload.from(List.of(A_SUBSCRIPTION));

        SubscriptionsPayload retrievedSubscriptions = subscriberService.getAllSubscriptions(A_SUBSCRIBER_ID);

        assertEquals(expectedSubscriptionsPayload, retrievedSubscriptions);
    }

    @Test
    public void whenGettingSubscription_shouldReturnMatchingSubscriptionPayload() {
        given(subscriberRepository.getById(A_SUBSCRIBER_ID)).willReturn(mockSubscriber);
        given(mockSubscriber.getSubscription(A_SUBSCRIPTION.getSubscriptionId())).willReturn(A_SUBSCRIPTION);
        SubscriptionPayload expectedSubscriptionPayload = SubscriptionPayload.from(A_SUBSCRIPTION);

        SubscriptionPayload retrievedSubscription = subscriberService.getSubscription(A_SUBSCRIBER_ID, A_SUBSCRIPTION.getSubscriptionId());

        assertEquals(expectedSubscriptionPayload, retrievedSubscription);
    }

    @Test
    public void whenGettingCurrentOrders_shouldReturnMatchingOrdersPayload() {
        given(subscriberRepository.getById(A_SUBSCRIBER_ID)).willReturn(mockSubscriber);
        given(mockSubscriber.getCurrentOrders()).willReturn(List.of(AN_ORDER));
        OrdersPayload expectedOrdersPayload = OrdersPayload.from(List.of(AN_ORDER));

        OrdersPayload retrievedCurrentOrders = subscriberService.getCurrentOrders(A_SUBSCRIBER_ID);

        assertEquals(expectedOrdersPayload, retrievedCurrentOrders);
    }

    @Test
    public void givenSporadicSubscription_whenConfirming_shouldSendMealKitConfirmedEvent() {
        ProcessConfirmationDto processConfirmationDto = mock(ProcessConfirmationDto.class);
        given(processConfirmationDto.confirmedOrders()).willReturn(List.of(AN_ORDER));
        given(subscriberRepository.getById(A_SUBSCRIBER_ID)).willReturn(mockSubscriber);
        given(mockSubscriber.confirm(A_SUBSCRIPTION.getSubscriptionId(), orderFactory, paymentService)).willReturn(Optional.of(processConfirmationDto));

        subscriberService.confirm(A_SUBSCRIBER_ID, A_SUBSCRIPTION.getSubscriptionId());

        verify(eventBus).publish(any(MealKitConfirmedEvent.class));
    }

    @Test
    public void whenProcessingOrders_shouldSendMealKitConfirmedEvent() {
        ProcessConfirmationDto processConfirmationDto = mock(ProcessConfirmationDto.class);
        given(processConfirmationDto.confirmedOrders()).willReturn(List.of(AN_ORDER));
        given(subscriberRepository.getAll()).willReturn(List.of(mockSubscriber));
        given(mockSubscriber.processOrders(paymentService)).willReturn(List.of(processConfirmationDto));

        subscriberService.processOrders();

        verify(eventBus).publish(any(MealKitConfirmedEvent.class));
    }

    @Test
    public void givenAssignedCardNumber_whenAddingCard_shouldThrowCardNumberAlreadyInUseException() {
        Subscriber subscriber = new SubscriberFixture().withSubscriberId(A_SUBSCRIBER_ID).withCardNumber(AN_UNASSIGNED_CARD_NUMBER).build();
        given(subscriberRepository.getById(A_SUBSCRIBER_ID)).willReturn(subscriber);
        given(subscriberRepository.cardNumberExists(AN_ASSIGNED_CARD_NUMBER)).willReturn(true);

        assertThrows(CardNumberAlreadyInUseException.class, () -> subscriberService.addCard(A_SUBSCRIBER_ID, AN_ASSIGNED_CARD_NUMBER));
    }

    @Test
    public void givenUnassignedCardNumber_whenAddingCard_shouldPublishSubscriberCardAddedEvent() {
        Subscriber subscriber = new SubscriberFixture().withSubscriberId(A_SUBSCRIBER_ID).build();
        given(subscriberRepository.getById(A_SUBSCRIBER_ID)).willReturn(subscriber);
        given(subscriberRepository.cardNumberExists(AN_UNASSIGNED_CARD_NUMBER)).willReturn(false);

        subscriberService.addCard(A_SUBSCRIBER_ID, AN_UNASSIGNED_CARD_NUMBER);

        verify(eventBus).publish(any(SubscriberCardAddedEvent.class));
    }
}
