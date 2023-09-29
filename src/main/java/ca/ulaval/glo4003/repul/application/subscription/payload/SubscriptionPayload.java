package ca.ulaval.glo4003.repul.application.subscription.payload;

import java.time.LocalDate;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.application.catalog.payload.LocationPayload;
import ca.ulaval.glo4003.repul.application.order.payload.OrdersPayload;
import ca.ulaval.glo4003.repul.domain.account.subscription.Frequency;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;

public record SubscriptionPayload(UniqueIdentifier subscriptionId, OrdersPayload ordersPayload, Frequency frequency, LocationPayload locationPayload,
                                  LocalDate startDate, LunchboxType lunchboxType) {
    public static SubscriptionPayload from(Subscription subscription) {
        return new SubscriptionPayload(subscription.getSubscriptionId(), OrdersPayload.from(subscription.getOrders()),
            subscription.getFrequency(), LocationPayload.from(subscription.getPickupLocation()), subscription.getStartDate(),
            subscription.getLunchboxType());
    }
}
