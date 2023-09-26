package ca.ulaval.glo4003.repul.fixture;

import java.util.Date;
import java.util.List;

import ca.ulaval.glo4003.repul.domain.account.subscription.Frequency;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;
import ca.ulaval.glo4003.repul.domain.account.subscription.SubscriptionId;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;

public class SubscriptionFixture {
    private SubscriptionId subscriptionId;
    private List<Order> orders;
    private Frequency frequency;
    private PickupLocation pickupLocation;
    private Date startDate;
    private LunchboxType lunchboxType;

    public SubscriptionFixture withSubscriptionId(SubscriptionId subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    public SubscriptionFixture withOrders(List<Order> orders) {
        this.orders = orders;
        return this;
    }

    public SubscriptionFixture withFrequency(Frequency frequency) {
        this.frequency = frequency;
        return this;
    }

    public SubscriptionFixture withPickupLocation(PickupLocation pickupLocation) {
        this.pickupLocation = pickupLocation;
        return this;
    }

    public SubscriptionFixture withStartDate(Date startDate) {
        this.startDate = startDate;
        return this;
    }

    public SubscriptionFixture withLunchboxType(LunchboxType lunchboxType) {
        this.lunchboxType = lunchboxType;
        return this;
    }

    public Subscription build() {
        return new Subscription(
            subscriptionId,
            orders,
            frequency,
            pickupLocation,
            startDate,
            lunchboxType
        );
    }
}
