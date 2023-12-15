package ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status;

import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;

public abstract class Status {
    protected Order order;

    public Status(Order order) {
        this.order = order;
    }

    public abstract OrderStatus getStatus();

    public abstract void confirm();

    public abstract void decline();

    public abstract boolean process(SubscriberUniqueIdentifier subscriberId, boolean isSporadic, PaymentService paymentService);
}
