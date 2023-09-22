package ca.ulaval.glo4003.repul.domain.account.subscription;

import java.util.Date;
import java.util.List;

import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;

public class Subscription {
    private SubscriptionId subscriptionId;
    private List<Order> orders;
    private Frequency frequency;
    private PickupLocation pickupLocation;
    private Date startDate;

    public Subscription(SubscriptionId subscriptionId, List<Order> orders, Frequency frequency, PickupLocation pickupLocation, Date startDate) {
        this.subscriptionId = subscriptionId;
        this.orders = orders;
        this.frequency = frequency;
        this.pickupLocation = pickupLocation;
        this.startDate = startDate;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public SubscriptionId getSubscriptionId() {
        return subscriptionId;
    }

    public PickupLocation getPickupLocation() {
        return pickupLocation;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public Date getStartDate() {
        return startDate;
    }
}
