package ca.ulaval.glo4003.repul.subscription.domain.subscription.order;

import java.time.LocalDate;
import java.time.LocalDateTime;

import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.MealKitUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.config.Config;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.ConfirmedStatus;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.DeclinedStatus;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.InDeliveryStatus;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.InPreparationStatus;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.OrderStatus;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.PendingStatus;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.PickedUpStatus;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.ReadyForPickUpStatus;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status.Status;

public class Order {
    private final LocalDate deliveryDate;
    private final MealKitUniqueIdentifier orderId;
    private final MealKitType mealKitType;
    private Status status;

    public Order(MealKitUniqueIdentifier orderId, MealKitType mealKitType, LocalDate deliveryDate) {
        this.orderId = orderId;
        this.mealKitType = mealKitType;
        this.deliveryDate = deliveryDate;
        this.status = new PendingStatus(this);
    }

    public Order(MealKitUniqueIdentifier orderId, MealKitType mealKitType, LocalDate deliveryDate, OrderStatus status) {
        this.orderId = orderId;
        this.mealKitType = mealKitType;
        this.deliveryDate = deliveryDate;
        this.status = getStatusFromOrderStatus(status);
    }

    private Status getStatusFromOrderStatus(OrderStatus orderStatus) {
        switch (orderStatus) {
            case PENDING:
                return new PendingStatus(this);
            case CONFIRMED:
                return new ConfirmedStatus(this);
            case DECLINED:
                return new DeclinedStatus(this);
            case IN_PREPARATION:
                return new InPreparationStatus(this);
            case IN_DELIVERY:
                return new InDeliveryStatus(this);
            case READY_FOR_PICK_UP:
                return new ReadyForPickUpStatus(this);
            case PICKED_UP:
                return new PickedUpStatus(this);
            default:
                throw new IllegalStateException("Unexpected value: " + orderStatus);
        }
    }

    public MealKitType getMealKitType() {
        return mealKitType;
    }

    public OrderStatus getOrderStatus() {
        return status.getStatus();
    }

    public void changeStatus(Status status) {
        this.status = status;
    }

    public MealKitUniqueIdentifier getOrderId() {
        return orderId;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public LocalDateTime getMaximumModificationDateTime() {
        LocalDateTime deliveryDateTime = LocalDateTime.of(deliveryDate, Config.getInstance().getOpeningTime());
        return deliveryDateTime.minus(Config.getInstance().getDurationToConfirm());
    }

    public void confirm() {
        status.confirm();
    }

    public void decline() {
        status.decline();
    }

    public boolean isActive() {
        return !deliveryDate.isBefore(LocalDate.now());
    }

    public boolean process(SubscriberUniqueIdentifier subscriberId, boolean isSporadic, PaymentService paymentService) {
        return status.process(subscriberId, isSporadic, paymentService);
    }
}
