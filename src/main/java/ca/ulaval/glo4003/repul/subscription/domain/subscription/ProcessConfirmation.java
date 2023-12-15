package ca.ulaval.glo4003.repul.subscription.domain.subscription;

import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;

public record ProcessConfirmation(
    SubscriptionUniqueIdentifier subscriptionId,
    Optional<DeliveryLocationId> deliveryLocationId,
    List<Order> confirmedOrders
) {
}
