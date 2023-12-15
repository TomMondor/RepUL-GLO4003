package ca.ulaval.glo4003.repul.fixture.subscription;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Frequency;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.SemesterCode;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Orders;

public class SubscriptionFixture {
    private SubscriptionUniqueIdentifier subscriptionId;
    private List<Order> orders;
    private Optional<Frequency> frequency;
    private Optional<DeliveryLocationId> deliveryLocationId;
    private LocalDate startDate;
    private Semester semester;
    private MealKitType mealKitType;

    public SubscriptionFixture() {
        subscriptionId = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
        orders = new ArrayList<>();
        frequency = Optional.of(new Frequency(DayOfWeek.FRIDAY));
        deliveryLocationId = Optional.of(DeliveryLocationId.VACHON);
        startDate = LocalDate.now();
        semester = new Semester(new SemesterCode("A23"), startDate.minusMonths(1), startDate.plusMonths(2));
        mealKitType = MealKitType.STANDARD;
    }

    public SubscriptionFixture withSubscriptionId(SubscriptionUniqueIdentifier subscriptionId) {
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

    public SubscriptionFixture withFrequency(Optional<Frequency> frequency) {
        this.frequency = frequency;
        return this;
    }

    public SubscriptionFixture withPickupLocationId(DeliveryLocationId deliveryLocationId) {
        this.deliveryLocationId = Optional.of(deliveryLocationId);
        return this;
    }

    public SubscriptionFixture withPickupLocationId(Optional<DeliveryLocationId> deliveryLocationId) {
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
        return new Subscription(subscriptionId, new Orders(orders), frequency, deliveryLocationId, startDate, semester, mealKitType);
    }
}
