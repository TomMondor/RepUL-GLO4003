package ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status;

import java.time.LocalDateTime;

import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderCannotBeConfirmedException;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;

public class PendingStatus extends Status {
    public PendingStatus(Order order) {
        super(order);
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.PENDING;
    }

    @Override
    public void confirm() {
        if (isAfterModificationTime()) {
            throw new OrderCannotBeConfirmedException();
        }
        order.changeStatus(new ConfirmedStatus(order));
    }

    @Override
    public void decline() {
        order.changeStatus(new DeclinedStatus(order));
    }

    @Override
    public boolean process(SubscriberUniqueIdentifier subscriberId, boolean isSporadic, PaymentService paymentService) {
        if (isAfterModificationTime()) {
            order.changeStatus(new DeclinedStatus(order));
        }

        return false;
    }

    private boolean isAfterModificationTime() {
        return LocalDateTime.now().isAfter(order.getMaximumModificationDateTime());
    }
}
