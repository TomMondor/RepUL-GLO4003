package ca.ulaval.glo4003.repul.small.subscription.domain.subscription;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationIdException;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionType;
import ca.ulaval.glo4003.repul.subscription.domain.exception.SemesterNotFoundException;
import ca.ulaval.glo4003.repul.subscription.domain.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Orders;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.OrdersFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionFactoryTest {
    private static final SubscriptionUniqueIdentifier A_UNIQUE_SUBSCRIPTION_IDENTIFIER = new SubscriptionUniqueIdentifier(UUID.randomUUID());
    private static final SubscriberUniqueIdentifier A_SUBSCRIBER_ID = new SubscriberUniqueIdentifier(UUID.randomUUID());
    private static final Semester CURRENT_SEMESTER = new Semester(new SemesterCode("A23"), LocalDate.now().minusDays(60), LocalDate.now().plusDays(17));
    private static final DeliveryLocationId A_VALID_DELIVERY_LOCATION_ID = DeliveryLocationId.VACHON;
    private static final DeliveryLocationId ANOTHER_VALID_DELIVERY_LOCATION_ID = DeliveryLocationId.PEPS;
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final DayOfWeek A_WEEKDAY = DayOfWeek.MONDAY;
    private static final SubscriptionType A_WEEKLY_TYPE = SubscriptionType.WEEKLY;
    private static final SubscriptionType A_SPORADIC_TYPE = SubscriptionType.SPORADIC;
    private static final Order AN_ORDER = new Order(new UniqueIdentifierFactory<>(MealKitUniqueIdentifier.class).generate(), A_MEALKIT_TYPE, LocalDate.now());
    private static final SubscriptionQuery A_WEEKLY_SUBSCRIPTION_QUERY = new SubscriptionQuery(A_WEEKLY_TYPE,
        A_SUBSCRIBER_ID, Optional.of(A_VALID_DELIVERY_LOCATION_ID),
        Optional.of(A_WEEKDAY), Optional.of(A_MEALKIT_TYPE));
    private static final SubscriptionQuery A_SPORADIC_SUBSCRIPTION_QUERY = new SubscriptionQuery(A_SPORADIC_TYPE,
        A_SUBSCRIBER_ID, Optional.empty(),
        Optional.empty(), Optional.empty());

    @Mock
    private OrdersFactory ordersFactory;
    @Mock
    private UniqueIdentifierFactory<SubscriptionUniqueIdentifier> subscriptionUniqueIdentifierFactory;

    private SubscriptionFactory subscriptionFactory;

    @BeforeEach
    public void createSubscriptionFactory() {
        subscriptionFactory =
            new SubscriptionFactory(subscriptionUniqueIdentifierFactory, ordersFactory, List.of(CURRENT_SEMESTER),
                List.of(A_VALID_DELIVERY_LOCATION_ID, ANOTHER_VALID_DELIVERY_LOCATION_ID));
    }

    @Test
    public void givenValidParameters_whenCreatingSubscription_shouldCreateSubscription() {
        when(subscriptionUniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_SUBSCRIPTION_IDENTIFIER);
        when(ordersFactory.createOrdersInSemester(any(), any(), any(), any())).thenReturn(new Orders(List.of(AN_ORDER)));
        DayOfWeek chosenDayOfWeek = DayOfWeek.from(LocalDate.now().plusDays(3));
        SubscriptionQuery subscriptionQuery = new SubscriptionQuery(A_WEEKLY_TYPE, A_SUBSCRIBER_ID,
            Optional.of(A_VALID_DELIVERY_LOCATION_ID), Optional.of(chosenDayOfWeek),Optional.of(A_MEALKIT_TYPE));

        Subscription subscription = subscriptionFactory.createSubscription(subscriptionQuery);

        assertEquals(A_UNIQUE_SUBSCRIPTION_IDENTIFIER, subscription.getSubscriptionId());
        assertEquals(DayOfWeek.from(chosenDayOfWeek), subscription.getFrequency().get().dayOfWeek());
        assertEquals(A_VALID_DELIVERY_LOCATION_ID, subscription.getDeliveryLocationId().get());
        assertEquals(LocalDate.now(), subscription.getStartDate());
        assertEquals(CURRENT_SEMESTER, subscription.getSemester());
        Assertions.assertEquals(A_MEALKIT_TYPE, subscription.getMealKitType());
        assertFalse(subscription.getCurrentOrder().isEmpty());
    }

    @Test
    public void givenUnsupportedLocationId_whenCreatingSubscription_shouldThrowInvalidLocationException() {
        DeliveryLocationId unsupportedDeliveryLocationId = DeliveryLocationId.VACHON;
        subscriptionFactory =
            new SubscriptionFactory(subscriptionUniqueIdentifierFactory, ordersFactory, List.of(CURRENT_SEMESTER), List.of());
        SubscriptionQuery subscriptionQuery = new SubscriptionQuery(A_WEEKLY_TYPE, A_SUBSCRIBER_ID,
            Optional.of(unsupportedDeliveryLocationId), Optional.of(A_WEEKDAY),Optional.of(A_MEALKIT_TYPE));

        assertThrows(InvalidLocationIdException.class,
            () -> subscriptionFactory.createSubscription(subscriptionQuery));
    }

    @Test
    public void givenCurrentDateNotInSupportedSemesters_whenCreatingSubscription_shouldThrowSemesterNotFoundException() {
        Semester alreadyEndedSemester = new Semester(new SemesterCode("A23"), LocalDate.now().minusDays(90), LocalDate.now().minusDays(3));
        subscriptionFactory =
            new SubscriptionFactory(subscriptionUniqueIdentifierFactory, ordersFactory, List.of(alreadyEndedSemester),
                List.of(A_VALID_DELIVERY_LOCATION_ID));

        assertThrows(SemesterNotFoundException.class,
            () -> subscriptionFactory.createSubscription(A_WEEKLY_SUBSCRIPTION_QUERY));
    }

    @Test
    public void whenCreatingSporadicSubscription_shouldCreateSubscriptionWithSpecifiedParameters() {
        when(subscriptionUniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_SUBSCRIPTION_IDENTIFIER);

        Subscription subscription = subscriptionFactory.createSubscription(A_SPORADIC_SUBSCRIPTION_QUERY);

        assertEquals(A_UNIQUE_SUBSCRIPTION_IDENTIFIER, subscription.getSubscriptionId());
        assertEquals(LocalDate.now(), subscription.getStartDate());
        assertEquals(CURRENT_SEMESTER, subscription.getSemester());
        Assertions.assertEquals(MealKitType.STANDARD, subscription.getMealKitType());
    }

    @Test
    public void whenCreatingSporadicSubscription_shouldCreateSubscriptionWithoutDeliveryLocationAndFrequency() {
        when(subscriptionUniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_SUBSCRIPTION_IDENTIFIER);

        Subscription subscription = subscriptionFactory.createSubscription(A_SPORADIC_SUBSCRIPTION_QUERY);

        assertFalse(subscription.getFrequency().isPresent());
        assertFalse(subscription.getDeliveryLocationId().isPresent());
    }

    @Test
    public void whenCreatingSporadicSubscription_shouldCreateSubscriptionWithoutOrders() {
        when(subscriptionUniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_SUBSCRIPTION_IDENTIFIER);

        Subscription subscription = subscriptionFactory.createSubscription(A_SPORADIC_SUBSCRIPTION_QUERY);

        assertTrue(subscription.getCurrentOrder().isEmpty());
    }
}
