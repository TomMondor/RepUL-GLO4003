package ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status;

import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderCannotBeConfirmedException;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderCannotBeDeclinedException;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;

public class ReadyForPickupStatus extends Status {
    public ReadyForPickupStatus(Order order) {
        super(order);
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.READY_FOR_PICK_UP;
    }

    @Override
    public void confirm() {
        throw new OrderCannotBeConfirmedException();
    }

    @Override
    public void decline() {
        throw new OrderCannotBeDeclinedException();
    }

    @Override
    public boolean process(SubscriberUniqueIdentifier subscriberId, boolean isSporadic, PaymentService paymentService) {
        return false;
    }
}
