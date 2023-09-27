package ca.ulaval.glo4003.repul.domain.account.subscription;

import java.time.LocalDate;
import java.util.List;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;
import ca.ulaval.glo4003.repul.domain.exception.NoNextOrderInSubscriptionException;

public class Subscription {
    private final UniqueIdentifier subscriptionId;
    private final List<Order> orders;
    private final Frequency frequency;
    private final PickupLocation pickupLocation;
    private final LocalDate startDate;
    private final LunchboxType lunchboxType;

    public Subscription(UniqueIdentifier subscriptionId, List<Order> orders, Frequency frequency, PickupLocation pickupLocation, LocalDate startDate,
                        LunchboxType lunchboxType) {
        this.subscriptionId = subscriptionId;
        this.orders = orders;
        this.frequency = frequency;
        this.pickupLocation = pickupLocation;
        this.startDate = startDate;
        this.lunchboxType = lunchboxType;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public UniqueIdentifier getSubscriptionId() {
        return subscriptionId;
    }

    public PickupLocation getPickupLocation() {
        return pickupLocation;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LunchboxType getLunchboxType() {
        return lunchboxType;
    }

    public void confirmNextLunchbox() {
        this.findNextOrder().confirm();
    }

    public void declineNextLunchbox() {
        this.findNextOrder().decline();
    }

    private Order findNextOrder() {
        return orders.stream().filter(Order::isInTheFuture).findFirst().orElseThrow(NoNextOrderInSubscriptionException::new);
    }
}
