package ca.ulaval.glo4003.repul.subscription.domain.subscription.order.status;

import java.time.LocalDateTime;

import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.PaymentService;
import ca.ulaval.glo4003.repul.subscription.domain.exception.OrderCannotBeDeclinedException;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;

public class ConfirmedStatus extends Status {
    public ConfirmedStatus(Order order) {
        super(order);
    }

    @Override
    public OrderStatus getStatus() {
        return OrderStatus.CONFIRMED;
    }

    @Override
    public void confirm() {
        // Order is already confirmed. Do nothing.
    }

    @Override
    public void decline() {
        if (!isInModificationPeriod()) {
            throw new OrderCannotBeDeclinedException();
        }
        order.changeStatus(new DeclinedStatus(order));
    }

    @Override
    public boolean process(SubscriberUniqueIdentifier subscriberId, boolean isSporadic, PaymentService paymentService) {
        if (isSporadic || !isInModificationPeriod()) {
            paymentService.pay(subscriberId, order.getMealKitType(), order.getDeliveryDate());

            order.changeStatus(new InPreparationStatus(order));

            return true;
        }
        return false;
    }

    private boolean isInModificationPeriod() {
        return LocalDateTime.now().isBefore(order.getMaximumModificationDateTime());
    }
}
