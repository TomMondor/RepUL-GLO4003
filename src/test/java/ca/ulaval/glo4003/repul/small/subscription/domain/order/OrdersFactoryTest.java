package ca.ulaval.glo4003.repul.small.subscription.domain.order;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.exception.NoOrdersInDesiredPeriodException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderCannotBeConfirmedException;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrderStatus;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrdersFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrdersFactoryTest {
    private static final MealKitUniqueIdentifier A_UNIQUE_IDENTIFIER = new MealKitUniqueIdentifier(UUID.randomUUID());
    private static final MealKitType A_MEALKIT_TYPE = MealKitType.STANDARD;
    private static final SemesterCode A_SEMESTER_CODE = new SemesterCode("123");
    private static final LocalDate A_START_DATE = LocalDate.now().minusMonths(2);
    private static final LocalDate AN_END_DATE = A_START_DATE.plusMonths(3);
    private static final Semester CURRENT_SEMESTER = new Semester(A_SEMESTER_CODE, A_START_DATE, AN_END_DATE);
    private OrdersFactory ordersFactory;
    @Mock
    private UniqueIdentifierFactory uniqueIdentifierFactory;

    @BeforeEach
    public void createOrdersFactory() {
        this.ordersFactory = new OrdersFactory(uniqueIdentifierFactory);
    }

    @Test
    public void givenTwoOccurrencesOfWeekdayBeforeEndOfSemester_whenCreatingOrders_shouldCreateListOfTwoOrders() {
        when(uniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_IDENTIFIER);
        LocalDate firstOrderDate = LocalDate.now().plusDays(5);
        DayOfWeek chosenDayOfWeek = DayOfWeek.from(firstOrderDate);
        long expectedOrdersCount = 2;
        LocalDate endOfSemester = firstOrderDate.plusDays((expectedOrdersCount - 1) * 7 + 1);
        List<Order> expectedOrders =
            List.of(new Order(A_UNIQUE_IDENTIFIER, A_MEALKIT_TYPE, firstOrderDate), new Order(A_UNIQUE_IDENTIFIER, A_MEALKIT_TYPE, firstOrderDate.plusDays(7)));

        List<Order> orders = ordersFactory.createOrdersInSemester(firstOrderDate, endOfSemester, chosenDayOfWeek, A_MEALKIT_TYPE);

        assertEquals(expectedOrdersCount, orders.size());
        assertOrders(expectedOrders.get(0), orders.get(0));
        assertOrders(expectedOrders.get(1), orders.get(1));
    }

    @Test
    public void givenTodayIsDesiredWeekdayAndThreeOccurrencesOfWeekdayBeforeEndOfSemester_whenCreatingOrders_shouldCreateListOfThreeOrders() {
        when(uniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_IDENTIFIER);
        LocalDate firstOrderDate = LocalDate.now();
        DayOfWeek chosenDayOfWeek = DayOfWeek.from(firstOrderDate);
        int expectedOrdersCount = 3;
        LocalDate endOfSemester = firstOrderDate.plusDays((expectedOrdersCount - 1) * 7 + 1);
        List<Order> expectedOrders =
            List.of(new Order(A_UNIQUE_IDENTIFIER, A_MEALKIT_TYPE, firstOrderDate), new Order(A_UNIQUE_IDENTIFIER, A_MEALKIT_TYPE, firstOrderDate.plusDays(7)),
                new Order(A_UNIQUE_IDENTIFIER, A_MEALKIT_TYPE, firstOrderDate.plusDays(14)));

        List<Order> orders = ordersFactory.createOrdersInSemester(firstOrderDate, endOfSemester, chosenDayOfWeek, A_MEALKIT_TYPE);

        assertEquals(expectedOrdersCount, orders.size());
        assertOrders(expectedOrders.get(0), orders.get(0));
        assertOrders(expectedOrders.get(1), orders.get(1));
        assertOrders(expectedOrders.get(2), orders.get(2));
    }

    @Test
    public void givenEndOfSemesterOnThirdDesiredWeekdayFromNow_whenCreatingOrders_shouldCreateListOfThreeOrders() {
        when(uniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_IDENTIFIER);
        LocalDate firstOrderDate = LocalDate.now().plusDays(3);
        DayOfWeek chosenDayOfWeek = DayOfWeek.from(firstOrderDate);
        int expectedOrdersCount = 3;
        LocalDate endOfSemester = firstOrderDate.plusDays((expectedOrdersCount - 1) * 7 + 1);
        List<Order> expectedOrders =
            List.of(new Order(A_UNIQUE_IDENTIFIER, A_MEALKIT_TYPE, firstOrderDate), new Order(A_UNIQUE_IDENTIFIER, A_MEALKIT_TYPE, firstOrderDate.plusDays(7)),
                new Order(A_UNIQUE_IDENTIFIER, A_MEALKIT_TYPE, firstOrderDate.plusDays(14)));

        List<Order> orders = ordersFactory.createOrdersInSemester(firstOrderDate, endOfSemester, chosenDayOfWeek, A_MEALKIT_TYPE);

        assertEquals(expectedOrdersCount, orders.size());
        assertOrders(expectedOrders.get(0), orders.get(0));
        assertOrders(expectedOrders.get(1), orders.get(1));
        assertOrders(expectedOrders.get(2), orders.get(2));
    }

    @Test
    public void givenNoOccurrenceOfDesiredWeekdayBeforeEndOfSemester_whenCreatingOrders_shouldThrowNoOrdersInDesiredPeriodException() {
        LocalDate firstOrderDate = LocalDate.now();
        LocalDate endOfSemester = firstOrderDate.plusDays(1);
        DayOfWeek chosenDayOfWeek = DayOfWeek.from(firstOrderDate.plusDays(2));

        assertThrows(NoOrdersInDesiredPeriodException.class,
            () -> ordersFactory.createOrdersInSemester(firstOrderDate, endOfSemester, chosenDayOfWeek, A_MEALKIT_TYPE));
    }

    @Test
    public void whenCreatingSporadicOrder_shouldUseProvidedParameters() {
        when(uniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_IDENTIFIER);

        Order order = ordersFactory.createSporadicOrder(CURRENT_SEMESTER, A_MEALKIT_TYPE);

        assertEquals(A_UNIQUE_IDENTIFIER, order.getOrderId());
        assertEquals(A_MEALKIT_TYPE, order.getMealKitType());
    }

    @Test
    public void whenCreatingSporadicOrder_shouldAlreadyBeToCook() {
        when(uniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_IDENTIFIER);

        Order order = ordersFactory.createSporadicOrder(CURRENT_SEMESTER, A_MEALKIT_TYPE);

        assertEquals(OrderStatus.TO_COOK, order.getOrderStatus());
    }

    @Test
    public void whenCreatingSporadicOrder_shouldHaveDateOfReceiptInTwoDays() {
        when(uniqueIdentifierFactory.generate()).thenReturn(A_UNIQUE_IDENTIFIER);
        LocalDate expectedDateOfReceipt = LocalDate.now().plusDays(2);

        Order order = ordersFactory.createSporadicOrder(CURRENT_SEMESTER, A_MEALKIT_TYPE);

        assertEquals(expectedDateOfReceipt, order.getDeliveryDate());
    }

    @Test
    public void givenSemesterEndsTomorrow_whenCreatingSporadicOrder_shouldThrowOrderCannotBeConfirmedException() {
        Semester semesterEndingTomorrow = new Semester(A_SEMESTER_CODE, A_START_DATE, LocalDate.now().plusDays(1));

        assertThrows(OrderCannotBeConfirmedException.class,
            () -> ordersFactory.createSporadicOrder(semesterEndingTomorrow, A_MEALKIT_TYPE));
    }

    private void assertOrders(Order expectedOrder, Order actualOrder) {
        assertEquals(expectedOrder.getOrderId(), actualOrder.getOrderId());
        assertEquals(expectedOrder.getDeliveryDate(), actualOrder.getDeliveryDate());
        assertEquals(expectedOrder.getMealKitType(), actualOrder.getMealKitType());
    }
}
