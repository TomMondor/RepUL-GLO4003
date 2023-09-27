package ca.ulaval.glo4003.repul.small.domain.account.subscription.order;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.OrderId;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.OrderStatus;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Ingredient;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Lunchbox;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Quantity;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.Recipe;
import ca.ulaval.glo4003.repul.domain.exception.OrderAlreadyStartedException;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    private static final Recipe A_RECIPE = new Recipe("A_RECIPE", 100, List.of(new Ingredient("Sucre", new Quantity(10, "g"))));
    private static final Order AN_ORDER_IN_THE_FUTURE =
        new Order(new Lunchbox(List.of(A_RECIPE)), new OrderId("AN_ID"), OrderStatus.PENDING, LocalDate.now().plusDays(3));
    private static final Order AN_ORDER_IN_THE_PAST =
        new Order(new Lunchbox(List.of(A_RECIPE)), new OrderId("AN_ID"), OrderStatus.PENDING, LocalDate.now().minusDays(3));

    @Test
    public void givenOrderInTheFuture_whenIsInTheFuture_shouldReturnTrue() {
        assertTrue(AN_ORDER_IN_THE_FUTURE.isInTheFuture());
    }

    @Test
    public void givenOrderInThePast_whenIsInTheFuture_shouldReturnFalse() {
        assertFalse(AN_ORDER_IN_THE_PAST.isInTheFuture());
    }

    @Test
    public void givenPendingOrder_whenConfirm_shouldUpdateStatusToToPrepare() {
        Order order = new Order(new Lunchbox(List.of(A_RECIPE)), new OrderId("AN_ID"), OrderStatus.PENDING, LocalDate.now().plusDays(3));

        order.confirm();

        assertEquals(OrderStatus.TO_PREPARE, order.getOrderStatus());
    }

    @Test
    public void givenStartedOrder_whenConfirm_shouldThrowOrderAlreadyStartedException() {
        Order order = new Order(new Lunchbox(List.of(A_RECIPE)), new OrderId("AN_ID"), OrderStatus.TO_DELIVER, LocalDate.now().plusDays(3));

        assertThrows(OrderAlreadyStartedException.class, order::confirm);
    }

    @Test
    public void givenDeclinedOrder_whenConfirm_shouldUpdateStatusToPrepare() {
        Order order = new Order(new Lunchbox(List.of(A_RECIPE)), new OrderId("AN_ID"), OrderStatus.DECLINED, LocalDate.now().plusDays(3));

        order.confirm();

        assertEquals(OrderStatus.TO_PREPARE, order.getOrderStatus());
    }

    @Test
    public void givenPendingOrder_whenDecline_shouldUpdateStatusToDeclined() {
        Order order = new Order(new Lunchbox(List.of(A_RECIPE)), new OrderId("AN_ID"), OrderStatus.PENDING, LocalDate.now().plusDays(3));

        order.decline();

        assertEquals(OrderStatus.DECLINED, order.getOrderStatus());
    }

    @Test
    public void givenStartedOrder_whenDecline_shouldThrowOrderAlreadyStartedException() {
        Order order = new Order(new Lunchbox(List.of(A_RECIPE)), new OrderId("AN_ID"), OrderStatus.TO_DELIVER, LocalDate.now().plusDays(3));

        assertThrows(OrderAlreadyStartedException.class, order::decline);
    }

    @Test
    public void givenConfirmedOrder_whenDecline_shouldUpdateStatusToDeclined() {
        Order order = new Order(new Lunchbox(List.of(A_RECIPE)), new OrderId("AN_ID"), OrderStatus.TO_PREPARE, LocalDate.now().plusDays(3));

        order.decline();

        assertEquals(OrderStatus.DECLINED, order.getOrderStatus());
    }

    @Test
    public void givenPendingOrderLessThan48HoursBeforeDelivery_whenGetOrderStatus_shouldUpdateStatusToDeclined() {
        Order order = new Order(new Lunchbox(List.of(A_RECIPE)), new OrderId("AN_ID"), OrderStatus.PENDING, LocalDate.now().plusDays(1));

        assertEquals(OrderStatus.DECLINED, order.getOrderStatus());
    }
}
