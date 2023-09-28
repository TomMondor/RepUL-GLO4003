package ca.ulaval.glo4003.repul.fixture;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ca.ulaval.glo4003.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.domain.account.subscription.Frequency;
import ca.ulaval.glo4003.repul.domain.account.subscription.Subscription;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.Order;
import ca.ulaval.glo4003.repul.domain.account.subscription.order.lunchbox.LunchboxType;
import ca.ulaval.glo4003.repul.domain.catalog.LocationId;
import ca.ulaval.glo4003.repul.domain.catalog.PickupLocation;

public class SubscriptionFixture {
    private UniqueIdentifier subscriptionId;
    private List<Order> orders;
    private Frequency frequency;
    private PickupLocation pickupLocation;
    private LocalDate startDate;
    private LunchboxType lunchboxType;

    public SubscriptionFixture() {
        this.subscriptionId = new UniqueIdentifier(UUID.randomUUID());
        this.orders = new ArrayList<>();
        this.frequency = new Frequency(DayOfWeek.FRIDAY);
        this.pickupLocation = new PickupLocation(new LocationId("pickupLocationId"), "pickupLocationName", 18);
        this.startDate = LocalDate.now();
        this.lunchboxType = LunchboxType.STANDARD;
    }

    public SubscriptionFixture withSubscriptionId(UniqueIdentifier subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    public SubscriptionFixture withOrders(List<Order> orders) {
        this.orders = orders;
        return this;
    }

    public SubscriptionFixture addOrder(Order order) {
        this.orders.add(order);
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

    public SubscriptionFixture withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public SubscriptionFixture withLunchboxType(LunchboxType lunchboxType) {
        this.lunchboxType = lunchboxType;
        return this;
    }

    public Subscription build() {
        return new Subscription(subscriptionId, orders, frequency, pickupLocation, startDate, lunchboxType);
    }
}
