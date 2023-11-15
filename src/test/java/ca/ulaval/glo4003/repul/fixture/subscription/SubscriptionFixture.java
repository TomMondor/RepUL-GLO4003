package ca.ulaval.glo4003.repul.fixture.subscription;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.Frequency;
import ca.ulaval.glo4003.repul.subscription.domain.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;

public class SubscriptionFixture {
    private UniqueIdentifier subscriberId;
    private UniqueIdentifier subscriptionId;
    private List<Order> orders;
    private Frequency frequency;
    private DeliveryLocationId deliveryLocationId;
    private LocalDate startDate;
    private Semester semester;
    private MealKitType mealKitType;

    public SubscriptionFixture() {
        subscriptionId = new UniqueIdentifierFactory().generate();
        subscriberId = new UniqueIdentifierFactory().generate();
        orders = new ArrayList<>();
        frequency = new Frequency(DayOfWeek.FRIDAY);
        deliveryLocationId = new DeliveryLocationId("location");
        startDate = LocalDate.now();
        semester = new Semester(new SemesterCode("A23"), startDate.minusMonths(1), startDate.plusMonths(2));
        mealKitType = MealKitType.STANDARD;
    }

    public SubscriptionFixture withSubscriptionId(UniqueIdentifier subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    public SubscriptionFixture withSubscriberId(UniqueIdentifier subscriberId) {
        this.subscriberId = subscriberId;
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

    public SubscriptionFixture withPickupLocationId(DeliveryLocationId deliveryLocationId) {
        this.deliveryLocationId = deliveryLocationId;
        return this;
    }

    public SubscriptionFixture withStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public SubscriptionFixture withSemester(Semester semester) {
        this.semester = semester;
        return this;
    }

    public SubscriptionFixture withMealKitType(MealKitType mealKitType) {
        this.mealKitType = mealKitType;
        return this;
    }

    public Subscription build() {
        return new Subscription(subscriptionId, subscriberId, orders, frequency, deliveryLocationId, startDate, semester, mealKitType);
    }
}
