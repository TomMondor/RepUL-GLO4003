package ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status;

import java.time.LocalDateTime;

import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderCannotBeConfirmedException;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;

public class DeclinedStatus extends Status {
    public DeclinedStatus(Order order) {
        super(order);
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.DECLINED;
    }

    @Override
    public void confirm() {
        if (!isInConfirmablePeriod()) {
            throw new OrderCannotBeConfirmedException();
        }
        order.changeStatus(new ConfirmedStatus(order));
    }

    @Override
    public void decline() {
        // Order is already declined. Do nothing.
    }

    @Override
    public boolean process(SubscriberUniqueIdentifier subscriberId, boolean isSporadic, PaymentService paymentService) {
        return false;
    }

    private boolean isInConfirmablePeriod() {
        return LocalDateTime.now().isBefore(order.getMaximumModificationDateTime());
    }
}
