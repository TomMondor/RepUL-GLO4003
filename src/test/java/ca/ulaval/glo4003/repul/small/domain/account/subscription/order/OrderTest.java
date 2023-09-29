package ca.ulaval.glo4003.repul.small.domain.account.subscription.order;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.OrderStatus;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Ingredient;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Quantity;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Recipe;
import ca.ulaval.glo4003.repul.domain.exception.OrderAlreadyStartedException;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    private static final UniqueIdentifier A_UNIQUE_IDENTIFIER = UniqueIdentifier.from(UUID.randomUUID().toString());
    private static final Recipe A_RECIPE = new Recipe("A_RECIPE", 100, List.of(new Ingredient("Sucre", new Quantity(10, "g"))));
    private static final LocalDate TODAY = LocalDate.now();
    private static final Order AN_ORDER_THE_SAME_DAY =
        new Order(A_UNIQUE_IDENTIFIER, new Lunchbox(List.of(A_RECIPE)), TODAY, OrderStatus.PENDING);

    private static final Order AN_ORDER_IN_THE_FUTURE =
        new Order(A_UNIQUE_IDENTIFIER, new Lunchbox(List.of(A_RECIPE)), LocalDate.now().plusDays(3), OrderStatus.PENDING);
    private static final Order AN_ORDER_IN_THE_PAST =
        new Order(A_UNIQUE_IDENTIFIER, new Lunchbox(List.of(A_RECIPE)), LocalDate.now().minusDays(3), OrderStatus.PENDING);

    @Test
    public void givenOrderToday_whenIsInTheFuture_shouldReturnTrue() {
        assertTrue(AN_ORDER_THE_SAME_DAY.isInTheFuture());
    }

    @Test
    public void givenOrderInTheFuture_whenIsInTheFuture_shouldReturnTrue() {
        assertTrue(AN_ORDER_IN_THE_FUTURE.isInTheFuture());
    }

    @Test
    public void givenOrderInThePast_whenIsInTheFuture_shouldReturnFalse() {
        assertFalse(AN_ORDER_IN_THE_PAST.isInTheFuture());
    }

    @Test
    public void givenPendingOrder_whenConfirm_shouldUpdateStatusToToCook() {
        Order order = new Order(A_UNIQUE_IDENTIFIER, new Lunchbox(List.of(A_RECIPE)), LocalDate.now().plusDays(3), OrderStatus.PENDING);

        order.confirm();

        assertEquals(OrderStatus.TO_COOK, order.getOrderStatus());
    }

    @Test
    public void givenStartedOrder_whenConfirm_shouldThrowOrderAlreadyStartedException() {
        Order order = new Order(A_UNIQUE_IDENTIFIER, new Lunchbox(List.of(A_RECIPE)), LocalDate.now().plusDays(3), OrderStatus.TO_DELIVER);

        assertThrows(OrderAlreadyStartedException.class, order::confirm);
    }

    @Test
    public void givenDeclinedOrder_whenConfirm_shouldUpdateStatusToToCook() {
        Order order = new Order(A_UNIQUE_IDENTIFIER, new Lunchbox(List.of(A_RECIPE)), LocalDate.now().plusDays(3), OrderStatus.DECLINED);

        order.confirm();

        assertEquals(OrderStatus.TO_COOK, order.getOrderStatus());
    }

    @Test
    public void givenPendingOrder_whenDecline_shouldUpdateStatusToDeclined() {
        Order order = new Order(A_UNIQUE_IDENTIFIER, new Lunchbox(List.of(A_RECIPE)), LocalDate.now().plusDays(3), OrderStatus.PENDING);

        order.decline();

        assertEquals(OrderStatus.DECLINED, order.getOrderStatus());
    }

    @Test
    public void givenStartedOrder_whenDecline_shouldThrowOrderAlreadyStartedException() {
        Order order = new Order(A_UNIQUE_IDENTIFIER, new Lunchbox(List.of(A_RECIPE)), LocalDate.now().plusDays(3), OrderStatus.TO_DELIVER);

        assertThrows(OrderAlreadyStartedException.class, order::decline);
    }

    @Test
    public void givenConfirmedOrder_whenDecline_shouldUpdateStatusToDeclined() {
        Order order = new Order(A_UNIQUE_IDENTIFIER, new Lunchbox(List.of(A_RECIPE)), LocalDate.now().plusDays(3), OrderStatus.TO_COOK);

        order.decline();

        assertEquals(OrderStatus.DECLINED, order.getOrderStatus());
    }

    @Test
    public void givenPendingOrderLessThan48HoursBeforeDelivery_whenGetOrderStatus_shouldUpdateStatusToDeclined() {
        Order order = new Order(A_UNIQUE_IDENTIFIER, new Lunchbox(List.of(A_RECIPE)), LocalDate.now().plusDays(1), OrderStatus.PENDING);

        assertEquals(OrderStatus.DECLINED, order.getOrderStatus());
    }
}
