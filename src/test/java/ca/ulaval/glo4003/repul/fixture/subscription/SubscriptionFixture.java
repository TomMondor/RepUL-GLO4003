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
import ca.ulaval.glo4003.repul.subscription.domain.subscription.Subscription;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.WeeklyOccurence;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Orders;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.SemesterCode;

public class SubscriptionFixture {
    private SubscriptionUniqueIdentifier subscriptionId;
    private List<Order> orders;
    private Optional<WeeklyOccurence> weeklyOccurence;
    private Optional<DeliveryLocationId> deliveryLocationId;
    private LocalDate startDate;
    private Semester semester;
    private MealKitType mealKitType;

    public SubscriptionFixture() {
        subscriptionId = new UniqueIdentifierFactory<>(SubscriptionUniqueIdentifier.class).generate();
        orders = new ArrayList<>();
        weeklyOccurence = Optional.of(new WeeklyOccurence(DayOfWeek.FRIDAY));
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

    public SubscriptionFixture withWeeklyOccurence(Optional<WeeklyOccurence> weeklyOccurence) {
        this.weeklyOccurence = weeklyOccurence;
        return this;
    }

    public SubscriptionFixture withPickUpLocationId(DeliveryLocationId deliveryLocationId) {
        this.deliveryLocationId = Optional.of(deliveryLocationId);
        return this;
    }

    public SubscriptionFixture withPickUpLocationId(Optional<DeliveryLocationId> deliveryLocationId) {
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
        return new Subscription(subscriptionId, new Orders(orders), weeklyOccurence, deliveryLocationId, startDate, semester, mealKitType);
    }
}
