package ca.ulaval.glo4003.repul.subscription.domain;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationIdException;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.exception.SemesterNotFoundException;
import ca.ulaval.glo4003.repul.subscription.domain.order.Order;
import ca.ulaval.glo4003.repul.subscription.domain.order.OrdersFactory;

public class SubscriptionFactory {
    private final UniqueIdentifierFactory uniqueIdentifierFactory;
    private final OrdersFactory ordersFactory;
    private final List<Semester> availableSemesters;
    private final List<DeliveryLocationId> availableDeliveryLocations;

    public SubscriptionFactory(UniqueIdentifierFactory uniqueIdentifierFactory, OrdersFactory ordersFactory, List<Semester> availableSemesters,
                               List<DeliveryLocationId> availableDeliveryLocations) {
        this.uniqueIdentifierFactory = uniqueIdentifierFactory;
        this.ordersFactory = ordersFactory;
        this.availableSemesters = availableSemesters;
        this.availableDeliveryLocations = availableDeliveryLocations;
    }

    public Subscription createSubscription(UniqueIdentifier subscriberId, DeliveryLocationId deliveryLocationId, DayOfWeek dayOfWeek, MealKitType mealKitType) {
        validateLocationId(deliveryLocationId);
        LocalDate today = LocalDate.now();
        Semester semester = getCurrentSemester(today);
        LocalDate semesterEndDate = semester.endDate();
        List<Order> orders = ordersFactory.createOrdersInSemester(today, semesterEndDate, dayOfWeek, mealKitType);

        return new Subscription(uniqueIdentifierFactory.generate(), subscriberId, orders, new Frequency(dayOfWeek), deliveryLocationId, today, semester,
            mealKitType);
    }

    private void validateLocationId(DeliveryLocationId deliveryLocationId) {
        if (!availableDeliveryLocations.contains(deliveryLocationId)) {
            throw new InvalidLocationIdException();
        }
    }

    private Semester getCurrentSemester(LocalDate currentDate) {
        return availableSemesters.stream().filter(semester -> semester.startDate().isBefore(currentDate) && semester.endDate().isAfter(currentDate)).findFirst()
            .orElseThrow(SemesterNotFoundException::new);
    }
}
