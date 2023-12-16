package ca.ulaval.glo4003.repul.subscription.domain.subscription;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import ca.ulaval.glo4003.repul.commons.domain.DeliveryLocationId;
import ca.ulaval.glo4003.repul.commons.domain.MealKitType;
import ca.ulaval.glo4003.repul.commons.domain.exception.InvalidLocationIdException;
import ca.ulaval.glo4003.repul.commons.domain.uid.SubscriptionUniqueIdentifier;
import ca.ulaval.glo4003.repul.commons.domain.uid.UniqueIdentifierFactory;
import ca.ulaval.glo4003.repul.subscription.domain.SubscriptionType;
import ca.ulaval.glo4003.repul.subscription.domain.exception.InvalidSubscriptionQueryException;
import ca.ulaval.glo4003.repul.subscription.domain.query.SubscriptionQuery;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.Orders;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.order.OrdersFactory;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.Semester;
import ca.ulaval.glo4003.repul.subscription.domain.subscription.semester.Semesters;

public class SubscriptionFactory {
    private final UniqueIdentifierFactory<SubscriptionUniqueIdentifier> subscriptionUniqueIdentifierFactory;
    private final OrdersFactory ordersFactory;
    private final Semesters availableSemesters;
    private final List<DeliveryLocationId> availableDeliveryLocations;

    public SubscriptionFactory(UniqueIdentifierFactory<SubscriptionUniqueIdentifier> subscriptionUniqueIdentifierFactory, OrdersFactory ordersFactory,
                               List<Semester> availableSemesters, List<DeliveryLocationId> availableDeliveryLocations) {
        this.subscriptionUniqueIdentifierFactory = subscriptionUniqueIdentifierFactory;
        this.ordersFactory = ordersFactory;
        this.availableSemesters = new Semesters(availableSemesters);
        this.availableDeliveryLocations = availableDeliveryLocations;
    }

    public Subscription createSubscription(SubscriptionQuery subscriptionQuery) {
        if (subscriptionQuery.subscriptionType().equals(SubscriptionType.SPORADIC)) {
            return createSporadicSubscription();
        } else {
            if (subscriptionQuery.locationId().isEmpty() || subscriptionQuery.dayOfWeek().isEmpty() || subscriptionQuery.mealKitType().isEmpty()) {
                throw new InvalidSubscriptionQueryException();
            }
            DeliveryLocationId deliveryLocationId = subscriptionQuery.locationId().get();
            DayOfWeek dayOfWeek = subscriptionQuery.dayOfWeek().get();
            MealKitType mealKitType = subscriptionQuery.mealKitType().get();
            return createWeeklySubscription(deliveryLocationId, dayOfWeek, mealKitType);
        }
    }

    private Subscription createWeeklySubscription(DeliveryLocationId deliveryLocationId, DayOfWeek dayOfWeek,
                                                  MealKitType mealKitType) {
        validateLocationId(deliveryLocationId);
        LocalDate today = LocalDate.now();
        Semester semester = getCurrentSemester(today);
        LocalDate semesterEndDate = semester.endDate();
        Orders orders = ordersFactory.createOrdersInSemester(today, semesterEndDate, dayOfWeek, mealKitType);
        Optional<WeeklyOccurence> weeklyOccurence = Optional.of(new WeeklyOccurence(dayOfWeek));

        return new Subscription(subscriptionUniqueIdentifierFactory.generate(), orders, weeklyOccurence, Optional.of(deliveryLocationId), today, semester,
            mealKitType);
    }

    private Subscription createSporadicSubscription() {
        LocalDate today = LocalDate.now();
        Semester semester = getCurrentSemester(today);

        return new Subscription(subscriptionUniqueIdentifierFactory.generate(), new Orders(), Optional.empty(), Optional.empty(), today, semester,
            MealKitType.STANDARD);
    }

    private void validateLocationId(DeliveryLocationId deliveryLocationId) {
        if (!availableDeliveryLocations.contains(deliveryLocationId)) {
            throw new InvalidLocationIdException();
        }
    }

    private Semester getCurrentSemester(LocalDate currentDate) {
        return availableSemesters.findSemesterByDate(currentDate);
    }
}
