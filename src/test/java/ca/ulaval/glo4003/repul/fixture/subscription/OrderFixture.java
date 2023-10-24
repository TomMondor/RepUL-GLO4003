package ca.ulaval.glo4003.repul.fixture.subscription;

import java.time.LocalDate;
import java.util.UUID;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrderStatus;

public class OrderFixture {
    private UniqueIdentifier orderId;
    private MealKitType mealKitType;
    private OrderStatus orderStatus;
    private LocalDate deliveryDate;

    public OrderFixture() {
        this.orderId = new UniqueIdentifier(UUID.randomUUID());
        this.mealKitType = MealKitType.STANDARD;
        this.orderStatus = OrderStatus.PENDING;
        this.deliveryDate = LocalDate.now().plusDays(4);
    }

    public OrderFixture withOrderId(UniqueIdentifier orderId) {
        this.orderId = orderId;
        return this;
    }

    public OrderFixture withMealKitType(MealKitType mealKitType) {
        this.mealKitType = mealKitType;
        return this;
    }

    public OrderFixture withOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
        return this;
    }

    public OrderFixture withDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
        return this;
    }

    public OrderFixture withDeliveryDateIn(long days) {
        this.deliveryDate = LocalDate.now().plusDays(days);
        return this;
    }

    public Order build() {
        return new Order(orderId, mealKitType, deliveryDate, orderStatus);
    }
}
