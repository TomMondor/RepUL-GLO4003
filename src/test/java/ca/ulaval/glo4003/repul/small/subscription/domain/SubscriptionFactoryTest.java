package ca.ulaval.glo4003.repul.small.subscription.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
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
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionFactory;
import ca.ulaval.glo4003.repul.subscription.domain.exception.NoOrdersInDesiredPeriodException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.SemesterNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubscriptionFactoryTest {
    private static final UniqueIdentifier A_UNIQUE_IDENTIFIER = new UniqueIdentifier(UUID.randomUUID());
    private static final UniqueIdentifier A_SUBSCRIBER_ID = new UniqueIdentifier(UUID.randomUUID());
    private static final Semester CURRENT_SEMESTER = new Semester(new SemesterCode("A23"), LocalDate.now().minusDays(60), LocalDate.now().plusDays(17));
    private static final DeliveryLocationId A_VALID_DELIVERY_LOCATION_ID = new DeliveryLocationId("VACHON");
    private static final DeliveryLocationId ANOTHER_VALID_DELIVERY_LOCATION_ID = new DeliveryLocationId("POULIOT");
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final DayOfWeek A_WEEKDAY = DayOfWeek.MONDAY;
    @Mock
    private UniqueIdentifierFactory uniqueIdentifierFactory;
    private SubscriptionFactory subscriptionFactory;

    @BeforeEach
    public void createSubscriptionFactory() {
        subscriptionFactory = new SubscriptionFactory(uniqueIdentifierFactory, List.of(CURRENT_SEMESTER),
            List.of(A_VALID_DELIVERY_LOCATION_ID, ANOTHER_VALID_DELIVERY_LOCATION_ID));
    }

    @Test
    public void givenValidParameters_whenCreatingSubscription_shouldCreateSubscription() {
        when(uniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_IDENTIFIER);
        DayOfWeek chosenDayOfWeek = DayOfWeek.from(LocalDate.now().plusDays(3));

        Subscription subscription = subscriptionFactory.createSubscription(A_SUBSCRIBER_ID, A_VALID_DELIVERY_LOCATION_ID, chosenDayOfWeek, A_MEALKIT_TYPE);

        assertEquals(A_UNIQUE_IDENTIFIER, subscription.getSubscriptionId());
        assertEquals(A_SUBSCRIBER_ID, subscription.getSubscriberId());
        assertEquals(DayOfWeek.from(chosenDayOfWeek), subscription.getFrequency().dayOfWeek());
        assertEquals(A_VALID_DELIVERY_LOCATION_ID, subscription.getDeliveryLocationId());
        assertEquals(LocalDate.now(), subscription.getStartDate());
        assertEquals(CURRENT_SEMESTER, subscription.getSemester());
        Assertions.assertEquals(A_MEALKIT_TYPE, subscription.getMealKitType());
        assertFalse(subscription.getOrders().isEmpty());
    }

    @Test
    public void givenTwoOccurrencesOfWeekdayBeforeEndOfSemester_whenCreatingSubscription_shouldCreateSubscriptionWithTwoOrders() {
        when(uniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_IDENTIFIER);
        LocalDate firstOrderDate = LocalDate.now().plusDays(5);
        DayOfWeek chosenDayOfWeek = DayOfWeek.from(firstOrderDate);
        long expectedOrdersCount = 2;

        Subscription subscription = subscriptionFactory.createSubscription(A_SUBSCRIBER_ID, A_VALID_DELIVERY_LOCATION_ID, chosenDayOfWeek, A_MEALKIT_TYPE);

        assertEquals(expectedOrdersCount, subscription.getOrders().size());
    }

    @Test
    public void givenTodayIsDesiredWeekdayAndThreeOccurrencesOfWeekdayBeforeEndOfSemester_whenCreatingSubscription_shouldCreateSubscriptionWithThreeOrders() {
        when(uniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_IDENTIFIER);
        LocalDate firstOrderDate = LocalDate.now();
        DayOfWeek chosenDayOfWeek = DayOfWeek.from(firstOrderDate);
        long expectedOrdersCount = 3;

        Subscription subscription = subscriptionFactory.createSubscription(A_SUBSCRIBER_ID, A_VALID_DELIVERY_LOCATION_ID, chosenDayOfWeek, A_MEALKIT_TYPE);

        assertEquals(expectedOrdersCount, subscription.getOrders().size());
    }

    @Test
    public void givenEndOfSemesterOnThirdDesiredWeekdayFromNow_whenCreatingSubscription_shouldCreateSubscriptionWithThreeOrders() {
        when(uniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_IDENTIFIER);
        LocalDate firstOrderDate = LocalDate.now().plusDays(3);
        DayOfWeek chosenDayOfWeek = DayOfWeek.from(firstOrderDate);
        long expectedOrdersCount = 3;

        Subscription subscription = subscriptionFactory.createSubscription(A_SUBSCRIBER_ID, A_VALID_DELIVERY_LOCATION_ID, chosenDayOfWeek, A_MEALKIT_TYPE);

        assertEquals(expectedOrdersCount, subscription.getOrders().size());
    }

    @Test
    public void givenNoOccurrenceOfDesiredWeekdayBeforeEndOfSemester_whenCreatingSubscription_shouldThrowNoOrdersInDesiredPeriodException() {
        Semester semesterEndingInThreeDays = new Semester(new SemesterCode("A23"), LocalDate.now().minusDays(60), LocalDate.now().plusDays(3));
        subscriptionFactory = new SubscriptionFactory(uniqueIdentifierFactory, List.of(semesterEndingInThreeDays), List.of(A_VALID_DELIVERY_LOCATION_ID));
        DayOfWeek chosenDayOfWeek = DayOfWeek.from(LocalDate.now().minusDays(1));

        assertThrows(NoOrdersInDesiredPeriodException.class,
            () -> subscriptionFactory.createSubscription(A_SUBSCRIBER_ID, A_VALID_DELIVERY_LOCATION_ID, chosenDayOfWeek, A_MEALKIT_TYPE));
    }

    @Test
    public void givenUnsupportedLocationId_whenCreatingSubscription_shouldThrowInvalidLocationException() {
        DeliveryLocationId unsupportedDeliveryLocationId = new DeliveryLocationId("UNSUPPORTED");
        subscriptionFactory =
            new SubscriptionFactory(uniqueIdentifierFactory, List.of(CURRENT_SEMESTER), List.of(A_VALID_DELIVERY_LOCATION_ID));

        assertThrows(InvalidLocationIdException.class,
            () -> subscriptionFactory.createSubscription(A_SUBSCRIBER_ID, unsupportedDeliveryLocationId, A_WEEKDAY, A_MEALKIT_TYPE));
    }

    @Test
    public void givenCurrentDateNotInSupportedSemesters_whenCreatingSubscription_shouldThrowSemesterNotFoundException() {
        Semester alreadyEndedSemester = new Semester(new SemesterCode("A23"), LocalDate.now().minusDays(90), LocalDate.now().minusDays(3));
        subscriptionFactory = new SubscriptionFactory(uniqueIdentifierFactory, List.of(alreadyEndedSemester), List.of(A_VALID_DELIVERY_LOCATION_ID));

        assertThrows(SemesterNotFoundException.class,
            () -> subscriptionFactory.createSubscription(A_SUBSCRIBER_ID, A_VALID_DELIVERY_LOCATION_ID, A_WEEKDAY, A_MEALKIT_TYPE));
    }
}
