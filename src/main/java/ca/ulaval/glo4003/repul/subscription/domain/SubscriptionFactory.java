package ca.ulaval.glo4003.repul.subscription.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationIdException;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriberUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.exception.SemesterNotFoundException;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrdersFactory;

public class SubscriptionFactory {
    private final UniqueIdentifierFactory<SubscriptionUniqueIdentifier> subscriptionUniqueIdentifierFactory;
    private final OrdersFactory ordersFactory;
    private final List<Semester> availableSemesters;
    private final List<DeliveryLocationId> availableDeliveryLocations;

    public SubscriptionFactory(UniqueIdentifierFactory<SubscriptionUniqueIdentifier> subscriptionUniqueIdentifierFactory, OrdersFactory ordersFactory,
                               List<Semester> availableSemesters, List<DeliveryLocationId> availableDeliveryLocations) {
        this.subscriptionUniqueIdentifierFactory = subscriptionUniqueIdentifierFactory;
        this.ordersFactory = ordersFactory;
        this.availableSemesters = availableSemesters;
        this.availableDeliveryLocations = availableDeliveryLocations;
    }

    public Subscription createSubscription(SubscriberUniqueIdentifier subscriberId, DeliveryLocationId deliveryLocationId, DayOfWeek dayOfWeek,
                                           MealKitType mealKitType) {
        validateLocationId(deliveryLocationId);
        LocalDate today = LocalDate.now();
        Semester semester = getCurrentSemester(today);
        LocalDate semesterEndDate = semester.endDate();
        List<Order> orders = ordersFactory.createOrdersInSemester(today, semesterEndDate, dayOfWeek, mealKitType);
        Optional<Frequency> frequency = Optional.of(new Frequency(dayOfWeek));

        return new Subscription(subscriptionUniqueIdentifierFactory.generate(), subscriberId, orders, frequency,
            Optional.of(deliveryLocationId), today, semester, mealKitType);
    }

    public Subscription createSubscription(SubscriberUniqueIdentifier subscriberId) {
        LocalDate today = LocalDate.now();
        Semester semester = getCurrentSemester(today);
        List<Order> orders = new ArrayList<>();

        return new Subscription(subscriptionUniqueIdentifierFactory.generate(), subscriberId, orders,
            Optional.empty(), Optional.empty(), today, semester, MealKitType.STANDARD);
    }

    private void validateLocationId(DeliveryLocationId deliveryLocationId) {
        if (!availableDeliveryLocations.contains(deliveryLocationId)) {
            throw new InvalidLocationIdException();
        }
    }

    private Semester getCurrentSemester(LocalDate currentDate) {
        return availableSemesters.stream().filter(semester -> semester.includes(currentDate)).findFirst()
            .orElseThrow(SemesterNotFoundException::new);
    }
}
